package com.perrier.music.coverart;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferDouble;
import java.awt.image.DataBufferFloat;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferShort;
import java.awt.image.DataBufferUShort;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.imageio.ImageIO;

import org.apache.commons.codec.digest.DigestUtils;
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
import com.perrier.music.tag.ITag;
import com.perrier.music.tag.Mp3Tag;

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

	private File getAlbumCoverFile(long id) throws DBException {

		Album album = this.albumProvider.findById(id);
		if (album == null || album.getCoverArt() == null) {
			return null;
		}

		String coversDir = this.config.getRequiredString(ApplicationProperties.COVERS_DIR);
		File file = new File(coversDir + File.separator + album.getCoverArt());

		return file;
	}

	private File getTrackCoverFile(long id) throws DBException {
		Track track = this.trackProvider.findById(id);
		if (track == null || track.getCoverArt() == null) {
			return null;
		}

		String coversDir = this.config.getRequiredString(ApplicationProperties.COVERS_DIR);
		File file = new File(coversDir + File.separator + track.getCoverArt());

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

	@Override
	public BufferedImage getCover(String path) throws IOException {
		if (this.isCached(path)) {
			File imageFile = new File(path);
			BufferedImage image = ImageIO.read(imageFile);
			return image;
		}

		return null;
	}

	@Override
	public String cacheCoverArt(BufferedImage image) throws IOException {
		// TODO: Don't scale for now. May want to cache different sizes of image for thumbnails or for a grid layout, etc..
		// BufferedImage scaledImg = scale(image, this.config.getOptionalInteger(ARTWORK_HEIGHT), this.config
		// .getOptionalInteger(ARTWORK_WIDTH));
		BufferedImage scaledImg = image;

		String extension = this.config.getOptionalString(ARTWORK_FORMAT);
		String imgName = this.generateImageName(scaledImg);

		String coverDir = this.config.getRequiredString(ApplicationProperties.COVERS_DIR);

		String imgPath = imgName + "." + extension;
		File imageFile = new File(coverDir + File.separator + imgPath);
		ImageIO.write(scaledImg, extension, imageFile);

		return imgPath;
	}

	private String generateImageName(BufferedImage image) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA1");

			byte[] bytes = null;
			final Raster raster = image.getRaster();
			final int dataType = raster.getDataBuffer().getDataType();

			switch (dataType) {
			case DataBuffer.TYPE_BYTE: {
				bytes = ((DataBufferByte) raster.getDataBuffer()).getData();
			}
			break;
			case DataBuffer.TYPE_INT: {
				int[] data = ((DataBufferInt) raster.getDataBuffer()).getData();
				ByteBuffer byteBuffer = ByteBuffer.allocate(data.length * Integer.BYTES);
				IntBuffer intBuffer = byteBuffer.asIntBuffer();
				intBuffer.put(data);
				bytes = byteBuffer.array();
			}
			break;
			case DataBuffer.TYPE_SHORT: {
				final short[] data = ((DataBufferShort) raster.getDataBuffer()).getData();
				ByteBuffer byteBuffer = ByteBuffer.allocate(data.length * Short.BYTES);
				ShortBuffer shortBuffer = byteBuffer.asShortBuffer();
				shortBuffer.put(data);
				bytes = byteBuffer.array();
			}
			case DataBuffer.TYPE_USHORT: {
				final short[] data = ((DataBufferUShort) raster.getDataBuffer()).getData();
				ByteBuffer byteBuffer = ByteBuffer.allocate(data.length * Short.BYTES);
				ShortBuffer shortBuffer = byteBuffer.asShortBuffer();
				shortBuffer.put(data);
				bytes = byteBuffer.array();
			}
			break;
			case DataBuffer.TYPE_FLOAT: {
				final float[] data = ((DataBufferFloat) raster.getDataBuffer()).getData();
				ByteBuffer byteBuffer = ByteBuffer.allocate(data.length * Float.BYTES);
				FloatBuffer floatBuffer = byteBuffer.asFloatBuffer();
				floatBuffer.put(data);
				bytes = byteBuffer.array();
			}
			break;
			case DataBuffer.TYPE_DOUBLE: {
				final double[] data = ((DataBufferDouble) raster.getDataBuffer()).getData();
				ByteBuffer byteBuffer = ByteBuffer.allocate(data.length * Double.BYTES);
				DoubleBuffer doubleBuffer = byteBuffer.asDoubleBuffer();
				doubleBuffer.put(data);
				bytes = byteBuffer.array();
			}
			break;
			case DataBuffer.TYPE_UNDEFINED:
			default:
				log.error("Unable to generate image path");
				break;
			}

			byte[] digest = md.digest(bytes);

			return DigestUtils.sha1Hex(digest);

		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Cannot generate image path", e);
		}
	}

	@Override
	public boolean isCached(String path) throws IOException {
		File image = new File(path);
		return image.isFile() && image.exists();
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

	public static void main(String[] args) throws Exception {
		File mp3File = new File(args[0]);
		ITag tag = Mp3Tag.parse(mp3File);

		CoverArtService cas = new CoverArtService(null, null, null);
		cas.generateImageName(tag.getCoverArt());
	}
}
