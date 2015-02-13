package com.perrier.music.tag;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.mp3.MP3AudioHeader;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.TagField;
import org.jaudiotagger.tag.id3.ID3v1Tag;
import org.jaudiotagger.tag.id3.ID3v24Frames;
import org.jaudiotagger.tag.id3.ID3v24Tag;
import org.jaudiotagger.tag.reference.GenreTypes;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.DateTimeParser;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO Perhaps this could be a "MP3MetaDataParser" class.
 * 
 * If a tracks source comes from an MP3, SoundCloud API, etc. then calling it a "Tag" doesn't make much sense
 * 
 */
public class Mp3Tag extends AbstractTag {

	private static final Logger log = LoggerFactory.getLogger(Mp3Tag.class);
	private static final Pattern GENRE_ID_TO_NAME_PATTERN = Pattern.compile("^\\(?(\\d+)\\)?$");

	/**
	 * The timestamp fields are based on a subset of ISO 8601
	 *
	 * Valid timestamps are:
	 *
	 * <ul>
	 * <li>yyyy</li>
	 * <li>yyyy-MM</li>
	 * <li>yyyy-MM-dd</li>
	 * <li>yyyy-MM-ddTHH</li>
	 * <li>yyyy-MM-ddTHH:mm</li>
	 * <li>yyyy-MM-ddTHH:mm:ss</li>
	 * </ul>
	 *
	 * All time stamps are UTC
	 */
	private static final DateTimeFormatter YEAR_FORMATTER;

	static {
		DateTimeParser[] parsers = { ISODateTimeFormat.year().getParser(), //
				ISODateTimeFormat.yearMonth().getParser(), //
				ISODateTimeFormat.yearMonthDay().getParser(), //
				ISODateTimeFormat.dateHour().getParser(), //
				ISODateTimeFormat.dateHourMinute().getParser(), //
				ISODateTimeFormat.dateHourMinuteSecond().getParser() //
		};

		YEAR_FORMATTER = new DateTimeFormatterBuilder().append(null, parsers).toFormatter();
	};

	private Mp3Tag(Builder builder) {
		super(builder);
	}

	public static ITag parse(File file) throws IOException {

		Mp3Tag tag = null;
		boolean hasTag = false;

		try {
			MP3File mp3File = (MP3File) AudioFileIO.read(file);
			Builder tagBuilder = new Builder();

			if (mp3File.hasID3v1Tag()) {
				parseID3v1Tag(mp3File, tagBuilder);
				hasTag = true;
			}

			if (mp3File.hasID3v2Tag()) {
				parseID3v2Tag(mp3File, tagBuilder);
				hasTag = true;
			}

			// if the file had no tags at all, we just set the track name to the file name
			if (!hasTag) {
				tagBuilder.track(FilenameUtils.removeExtension(file.getName()));
			}

			// Set the length of the track
			setLength(mp3File, tagBuilder);

			tag = tagBuilder.build();

		} catch (Exception e) {
			log.error("Error parsing MP3 tag", e);
		}

		return tag;
	}

	/**
	 * ID3v1 tags used a numeric id for the genre. Some software (looking at you iTunes) uses the numeric value in ID3v2
	 * tags as well, even though they are supposed to be actual genre names.
	 * 
	 * To be nice, we'll check if genre field is a number (i.e.: 52 = House) and return the real genre name, otherwise
	 * return the existing value. The number can also be wrapped in parentheses
	 * 
	 * @param rawGenre
	 * @return
	 */
	private static String setGenre(String rawGenre) {
		String genreName = rawGenre;

		Matcher m = GENRE_ID_TO_NAME_PATTERN.matcher(genreName);
		if (m.matches()) {
			int genreId = Integer.parseInt(m.group(1));
			String realGenre = GenreTypes.getInstanceOf().getValueForId(genreId);
			if (realGenre != null) {
				genreName = realGenre;
			}
		}

		return genreName;
	}

	private static void parseID3v2Tag(MP3File f, Builder b) {

		ID3v24Tag v2tag = f.getID3v2TagAsv24();

		b.artist(getID3v24TagValue(v2tag, FieldKey.ARTIST));
		b.albumArtist(getID3v24TagValue(v2tag, FieldKey.ALBUM_ARTIST));
		b.album(getID3v24TagValue(v2tag, FieldKey.ALBUM));
		b.track(setTrack(getID3v24TagValue(v2tag, FieldKey.TITLE), f.getFile()));

		String rawYear = getID3v24TagValue(v2tag, FieldKey.YEAR);
		try {
			b.year(parseYear(rawYear));
		} catch (Exception e) {
			log.warn("Unable to parse year: file={}, year={}", f, rawYear);
		}

		b.genre(setGenre(getID3v24TagValue(v2tag, FieldKey.GENRE)));
		b.number(setTrackNumber(v2tag.getFirst(ID3v24Frames.FRAME_ID_TRACK)));
		b.coverArt(setCoverArt(v2tag.getFirstArtwork()));
	}

	private static String parseYear(String rawTimestampField) throws Exception {
		if (rawTimestampField == null) {
			return null;
		}

		DateTime dateTime = YEAR_FORMATTER.parseDateTime(rawTimestampField);
		return Integer.toString(dateTime.getYear());
	}

	private static String setTrack(String track, File file) {
		// Use the file name as the track name if it's blank
		if (track == null) {
			track = FilenameUtils.removeExtension(file.getName());
		}

		return track;
	}

	private static String getID3v24TagValue(ID3v24Tag tag, final FieldKey field) {
		String value = tag.getFirst(field);
		return (!StringUtils.isBlank(value) ? value : null);
	}

	private static void setLength(MP3File f, Builder b) {
		MP3AudioHeader audioHeader = f.getMP3AudioHeader();
		double lengthInSeconds = audioHeader.getPreciseTrackLength();
		// convert to milliseconds & round
		long lengthInMillis = Math.round(lengthInSeconds * 1000);
		b.length(lengthInMillis);
	}

	private static void parseID3v1Tag(MP3File f, Builder b) {

		try {
			ID3v1Tag tag = f.getID3v1Tag();

			b.artist(getID3v1TagValue(tag.getArtist()));
			b.album(getID3v1TagValue(tag.getAlbum()));
			b.track(setTrack(getID3v1TagValue(tag.getTitle()), f.getFile()));
			b.year(getID3v1TagValue(tag.getYear()));
			b.genre(setGenre(getID3v1TagValue(tag.getGenre())));
			// ID3v1 tags have no track numbers
			// ID3v1 tags have no cover art

		} catch (final Exception e) {
			log.warn("Could not parse ID3v1 tag, file: {}", f, e);
		}
	}

	private static String getID3v1TagValue(List<TagField> fields) {

		String value = null;

		if (!fields.isEmpty()) {
			value = fields.get(0).toString();
		}

		return value;
	}

	public final static class Builder extends AbstractTagBuilder<Mp3Tag> {

		@Override
		public Mp3Tag build() {
			return new Mp3Tag(this);
		}

		@Override
		public String toString() {
			return "Builder [toString()=" + super.toString() + "]";
		}
	}

	@Override
	public String toString() {
		return Mp3Tag.class.getSimpleName() + super.toString();
	}
}
