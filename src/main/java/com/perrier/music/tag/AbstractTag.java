package com.perrier.music.tag;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.jaudiotagger.tag.datatype.Artwork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractTag implements ITag {

	private static final Logger log = LoggerFactory.getLogger(AbstractTag.class);

	private static final Pattern TRACK_NUMBER_TOTAL_PATTERN = Pattern.compile("(\\d+)/\\d+");

	protected final String track;
	protected final String artist;
	protected final String albumArtist;
	protected final String album;
	protected final String genre;
	protected final Integer number;
	protected final Long length;
	protected final String year;
	protected final BufferedImage coverArt;

	protected AbstractTag(AbstractTagBuilder<? extends ITag> b) {
		this.track = b.track;
		this.artist = b.artist;
		this.albumArtist = b.albumArtist;
		this.album = b.album;
		this.genre = b.genre;
		this.number = b.number;
		this.length = b.length;
		this.year = b.year;
		this.coverArt = b.coverArt;
	}

	@Override
	public String getArtist() {
		return this.artist;
	}

	@Override
	public String getAlbum() {
		return this.album;
	}

	@Override
	public String getAlbumArtist() {
		return this.albumArtist;
	}

	@Override
	public String getTrack() {
		return this.track;
	}

	@Override
	public Integer getNumber() {
		return this.number;
	}

	@Override
	public Long getLength() {
		return this.length;
	}

	@Override
	public String getYear() {
		return this.year;
	}

	@Override
	public String getGenre() {
		return this.genre;
	}

	@Override
	public BufferedImage getCoverArt() {
		return this.coverArt;
	}

	protected static BufferedImage setCoverArt(final Artwork artwork) {
		BufferedImage coverArt = null;

		if (artwork != null) {
			try {
				coverArt = artwork.getImage();
			} catch (IOException | NullPointerException e) {
				// NPE seen on some tags getting image. Bug in jaudiotagger lib?
				log.warn("Could not read cover art from tag");
				log.debug("Could not read cover art from tag", e);
			}
		}

		return coverArt;
	}

	protected static Integer setTrackNumber(final String rawTrackNumber) {
		Integer number = null;

		if (!StringUtils.isBlank(rawTrackNumber)) {
			String trackNumber = removeTrackTotalIfPresent(rawTrackNumber);

			try {
				int num = Integer.parseInt(trackNumber);
				// only set if positive integer
				if (num > 0) {
					number = num;
				}
			} catch (final NumberFormatException e) {
				log.warn("Could not parse track number: {}", trackNumber);
			}
		}

		return number;
	}

	/**
	 * Some tags store track number and total tracks (example: 01/12)
	 * <p>
	 * Returns the track number "01", removing the total
	 *
	 * @param trackNumber
	 * @return
	 */
	protected static String removeTrackTotalIfPresent(String trackNumber) {
		Matcher m = TRACK_NUMBER_TOTAL_PATTERN.matcher(trackNumber);
		return m.matches() ? m.group(1) : trackNumber;
	}

	public abstract static class AbstractTagBuilder<T extends ITag> {

		private String track;
		private String artist;
		private String albumArtist;
		private String album;
		private String genre;
		private Integer number;
		private Long length;
		private String year;
		private BufferedImage coverArt;

		public AbstractTagBuilder<T> track(String track) {
			this.track = track;
			return this;
		}

		public AbstractTagBuilder<T> artist(String artist) {
			this.artist = artist;
			return this;
		}

		public AbstractTagBuilder<T> albumArtist(String albumArtist) {
			this.albumArtist = albumArtist;
			return this;
		}

		public AbstractTagBuilder<T> album(String album) {
			this.album = album;
			return this;
		}

		public AbstractTagBuilder<T> genre(String genre) {
			this.genre = genre;
			return this;
		}

		public AbstractTagBuilder<T> number(Integer number) {
			this.number = number;
			return this;
		}

		public AbstractTagBuilder<T> length(Long length) {
			this.length = length;
			return this;
		}

		public AbstractTagBuilder<T> year(String year) {
			this.year = year;
			return this;
		}

		public AbstractTagBuilder<T> coverArt(BufferedImage coverArt) {
			this.coverArt = coverArt;
			return this;
		}

		public abstract T build();
	}

	@Override
	public String toString() {
		return "[track=" + this.track + ", artist=" + this.artist + ", albumArtist=" + this.albumArtist + ", album="
				+ this.album + ", genre=" + this.genre + ", number=" + this.number + ", length=" + this.length + ", year="
				+ this.year + ", coverArt?=" + (this.coverArt != null) + "]";
	}

}
