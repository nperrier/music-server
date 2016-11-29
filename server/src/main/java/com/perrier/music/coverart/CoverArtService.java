package com.perrier.music.coverart;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.AbstractIdleService;
import com.google.inject.Inject;
import com.perrier.music.ApplicationProperties;
import com.perrier.music.config.IConfiguration;
import com.perrier.music.config.OptionalProperty;
import com.perrier.music.config.Property;
import com.perrier.music.db.DBException;
import com.perrier.music.entity.album.Album;
import com.perrier.music.entity.album.AlbumProvider;
import com.perrier.music.entity.track.Track;
import com.perrier.music.entity.track.TrackProvider;

// TODO: S3
public class CoverArtService extends AbstractIdleService implements ICoverArtService {

	private static final Logger log = LoggerFactory.getLogger(CoverArtService.class);

	public static final OptionalProperty<Integer> ARTWORK_WIDTH = new OptionalProperty<>("coverart.width", 115);
	public static final OptionalProperty<Integer> ARTWORK_HEIGHT = new OptionalProperty<>("coverart.height", 115);
	public static final OptionalProperty<String> ARTWORK_FORMAT = new OptionalProperty<>("coverart.format", "png");
	public static final Property<String> ARTWORK_DEFAULT = new Property<>("coverart.nocover");

	private final IConfiguration config;
	private final TrackProvider trackProvider;
	private final AlbumProvider albumProvider;

	public enum Type {
		ARTIST, ALBUM, TRACK
	}

	@Inject
	public CoverArtService(IConfiguration config, TrackProvider trackProvider, AlbumProvider albumProvider) {
		this.config = config;
		this.trackProvider = trackProvider;
		this.albumProvider = albumProvider;
	}

	@Override
	protected void startUp() throws Exception {
	}

	@Override
	protected void shutDown() throws Exception {
	}

	@Override
	public File getCoverFile(Type type, Long id) throws CoverArtException {
		try {
			File coverArt = null;

			switch (type) {
			case ALBUM:
				coverArt = this.getAlbumCoverFile(id);
				if (coverArt == null) {
					// TODO check if any tracks have cover art before sending default?
					return this.getDefaultCoverArt(type);
				}
				return coverArt;
			case ARTIST:
				throw new UnsupportedOperationException("Not yet implemented");
			case TRACK:
				coverArt = this.getTrackCoverFile(id);
				if (coverArt == null) {
					return this.getDefaultCoverArt(type);
				}
				return coverArt;
			default:
				throw new RuntimeException("Unknown Cover Type: " + type);
			}
		} catch (DBException e) {
			throw new CoverArtException("Unable to get cover file", e);
		}
	}

	// TODO: convert to s3
	private File getAlbumCoverFile(long id) throws DBException {

		Album album = this.albumProvider.findById(id);
		if (album == null || album.getCoverStorageKey() == null) {
			return null;
		}

		String coversDir = this.config.getRequiredString(ApplicationProperties.COVERS_DIR);
		File file = new File(coversDir + File.separator + album.getCoverStorageKey());

		return file;
	}

	private File getTrackCoverFile(long id) throws DBException {
		Track track = this.trackProvider.findById(id);
		if (track == null || track.getCoverStorageKey() == null) {
			return null;
		}

		String coversDir = this.config.getRequiredString(ApplicationProperties.COVERS_DIR);
		File file = new File(coversDir + File.separator + track.getCoverStorageKey());

		return file;
	}

	/**
	 * TODO
	 * <p>
	 * Returns generic artwork
	 *
	 * @param type
	 * @return
	 */
	private File getDefaultCoverArt(Type type) {
		String resourcesDir = this.config.getRequiredString(ApplicationProperties.RESOURCES_DIR);
		return new File(resourcesDir + "/images/" + this.config.getRequiredString(ARTWORK_DEFAULT));
	}

	private static BufferedImage scale(BufferedImage image, int width, int height) {
		int origWidth = image.getWidth();
		int origHeight = image.getHeight();

		// check if we need to resize at all
		if (width == origWidth && height == origHeight) {
			return image;
		}

		return scale(image, calcScalingFactor(origWidth, origHeight, width, height));
	}

	/**
	 * Scale the image by the specified factor
	 *
	 * @param image
	 * @param dScaleFactor
	 * @return
	 */
	private static BufferedImage scale(BufferedImage image, double dScaleFactor) {
		// calculate new width and height
		int width = (int) (image.getWidth() * dScaleFactor);
		int height = (int) (image.getHeight() * dScaleFactor);

		// create a BufferedImage instance
		BufferedImage bufferedImage = new BufferedImage(width, height, image.getType());

		// create the image's graphics
		Graphics2D g = bufferedImage.createGraphics();

		// Drawing hints with focus on quality
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

		// Apply scale factor
		g.drawImage(image, 0, 0, width, height, null);

		return bufferedImage;
	}

	/**
	 * Calculate the factor to scale the image by
	 *
	 * @param srcWidth
	 * @param srcHeight
	 * @param targetWidth
	 * @param targetHeight
	 * @return
	 */
	private static double calcScalingFactor(int srcWidth, int srcHeight, int targetWidth, int targetHeight) {
		boolean tall = (srcHeight > srcWidth);
		double factor = (double) (tall ? targetHeight : targetWidth) / (double) (tall ? srcHeight : srcWidth);

		return factor;
	}

}
