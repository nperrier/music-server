package com.perrier.music.indexer;

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
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.BoundedInputStream;
import org.apache.commons.lang3.StringUtils;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.mp3.MP3File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.perrier.music.api.LibraryMetaData;
import com.perrier.music.api.TrackMetaData;
import com.perrier.music.tag.ITag;
import com.perrier.music.tag.Mp3Tag;

/**
 * Recursively scans a directory for tracks to index
 */
public class Mp3DirectoryScanner {

	private static final Logger log = LoggerFactory.getLogger(Mp3DirectoryScanner.class);

	private static final FileFilter songFileFilter = new MusicFileFilter();

	private final TrackUploaderService uploaderService;
	private final ExecutorService executor;

	private final File path;
	// audio hash -> track meta data
	// hash lookup for cover image so we don't upload covers multiple times if they already exist
	private final Set<String> coverMetaDatas;
	private final Map<String, TrackMetaData> trackMetaDatas;

	private boolean cancelScan = false;

	private static class MusicFileFilter implements FileFilter {

		@Override
		public boolean accept(File f) {
			boolean accept = f.exists() && f.canRead() && (f.isDirectory() || (f.isFile() && f.getName().endsWith(".mp3")));
			return accept;
		}
	}

	public Mp3DirectoryScanner(String path, LibraryMetaData libraryMetaData, TrackUploaderService uploaderService) {
		this.path = new File(path);
		this.uploaderService = uploaderService;
		this.executor = Executors.newSingleThreadExecutor();
		this.trackMetaDatas = libraryMetaData.getTrackMetaData().stream()
				.collect(Collectors.toMap(TrackMetaData::getAudioHash, t -> t));
		this.coverMetaDatas = libraryMetaData.getTrackMetaData().stream()
				.map(TrackMetaData::getCoverHash)
				.filter(Objects::nonNull)
				.collect(Collectors.toSet());
	}

	public void start() throws Exception {
		if (!this.path.exists() || !this.path.canRead() || !this.path.isDirectory()) {
			throw new Exception("Unable to index " + this.path + ": directory doesn't exist or cannot read");
		}

		this.scan(this.path);

		if (!this.cancelScan) {
			// TODO: Update server with lastIndexedDate set to now
		}
	}

	public void stop() {
		this.cancelScan = true;
		this.executor.shutdownNow();
	}

	private void scan(File dir) {
		if (this.cancelScan) {
			return;
		}

		// Search for all files in dir
		for (File file : dir.listFiles(songFileFilter)) {

			// check that we're not being told to stop - called from Executor.shutdownNow()
			if (Thread.currentThread().isInterrupted()) {
				log.debug("Stopping scan: thread was interrupted");
				this.cancelScan = true;
				return;
			}

			if (file.isDirectory()) {
				this.scan(file);
			} else {
				try {
					this.index(file);
				} catch (Exception e) {
					log.warn("Unable to index file: {}", file, e);
				}
			}
		}
	}

	public void index(File file) throws Exception {
		log.info("Indexing file: {}", file);

		// calculate audio hash
		// TODO: Cache hashes in MP3 file's tag so we don't need to recalculate them every time.
		// Then just check if the special tag frame exists and use it to ID the file
		byte[] audioData = getAudioData(file);
		String audioDataHash = createHash(audioData);
		// lookup in map to see if the track has been indexed already
		TrackMetaData idxTrack = this.trackMetaDatas.get(audioDataHash);

		long fileModDate = file.lastModified();

		log.trace("Audio Date for file {}: {} bytes, hash: {}, modDate: {}", file, audioData.length, audioDataHash,
				new Date(fileModDate));

		// don't bother indexing if track is not 'Editable'
		if (idxTrack != null && idxTrack.getEdited()) {
			log.info("Skipping track that is marked as 'edited', track={}", idxTrack);
		} else if (idxTrack != null && idxTrack.isIndexed()) {
			// corner case: check if the file was already indexed
			// TODO: If two tracks with the same audio hash were found it could be one of two cases:
			// 1. two different audio files have the same hash (very unlikely)
			// -> check audio length, file size, etc.. to determine if it's a hash collision
			// 2. two copies of the same audio file exist (more likely)
			// log and skip for now. possibly future: upload meta data and create new track linked to same audio file
			log.warn("File found with same audio hash as indexed track. Skipping. file: {}, track: {}", file,
					idxTrack);
		} else if (idxTrack != null) {

			// quick check: file modification date
			if (fileModDate != idxTrack.getFileModificationDate()) {
				// check meta data
				ITag fileTag = Mp3Tag.parse(file);
				boolean changed = compareMetaData(fileTag, idxTrack);

				// check cover art
				BufferedImage fileCoverArt = fileTag.getCoverArt();
				ByteBuffer coverData = null;
				String fileCoverHash = null;
				if (fileCoverArt != null) {
					coverData = getCoverData(fileCoverArt);
					fileCoverHash = createHash(coverData.array());
				}
				boolean coverChanged = !Objects.equals(fileCoverHash, idxTrack.getCoverHash());
				boolean sendCover = !this.coverMetaDatas.contains(fileCoverHash);

				if (changed || coverChanged) {
					// CHANGED
					// send to server
					if (sendCover) {
						this.coverMetaDatas.add(fileCoverHash);
						// TODO: send cover
					} else {
						// TODO: don't send cover
					}

					// TODO: upload track

				} else {
					// NO CHANGE: continue
					log.trace("File modification date changed, but meta data has not");
				}
			} else {
				// NO CHANGE: continue
				log.trace("File modification date has not changed, skipping");
			}

			idxTrack.setIndexed(true);
		} else {
			// NEW
			// extract meta-data
			ITag fileTag = Mp3Tag.parse(file);

			// extract cover art
			BufferedImage coverImage = fileTag.getCoverArt();
			String coverImageHash = null;
			if (coverImage != null) {
				ByteBuffer coverData = getCoverData(coverImage);
				coverImageHash = createHash(coverData.array());
			}

			TrackMetaData trackMetaData = this.uploaderService.uploadTrack(fileModDate, fileTag, coverImage, coverImageHash,
					audioData, audioDataHash);

			// update index maps with new track & cover hashes
			this.trackMetaDatas.put(audioDataHash, trackMetaData);
			this.coverMetaDatas.add(coverImageHash);
		}
	}

	private static boolean compareMetaData(ITag fileTag, TrackMetaData idxTrack) throws IOException {

		String albumName = StringUtils.trim(fileTag.getAlbum());
		if (!Objects.equals(albumName, idxTrack.getAlbum())) {
			return true;
		}

		String albumArtist = StringUtils.trim(fileTag.getAlbumArtist());
		if (!Objects.equals(albumArtist, idxTrack.getAlbumArtist())) {
			return true;
		}

		String artist = StringUtils.trim(fileTag.getArtist());
		if (!Objects.equals(artist, idxTrack.getArtist())) {
			return true;
		}

		String genre = StringUtils.trim(fileTag.getGenre());
		if (!Objects.equals(genre, idxTrack.getGenre())) {
			return true;
		}

		String track = StringUtils.trim(fileTag.getTrack());
		if (!track.equals(idxTrack.getName())) {
			return true;
		}

		String year = StringUtils.trim(fileTag.getYear());
		if (!Objects.equals(year, idxTrack.getYear())) {
			return true;
		}

		if (!Objects.equals(fileTag.getLength(), idxTrack.getLength())) {
			return true;
		}

		if (!Objects.equals(fileTag.getNumber(), idxTrack.getNumber())) {
			return true;
		}

		return false;
	}

	/**
	 * Extracts bytes from a BufferedImage
	 *
	 * @param image
	 * @return
	 */
	private static ByteBuffer getCoverData(BufferedImage image) {
		ByteBuffer byteBuffer = null;

		final Raster raster = image.getRaster();
		final int dataType = raster.getDataBuffer().getDataType();

		switch (dataType) {
		case DataBuffer.TYPE_BYTE: {
			byte[] data = ((DataBufferByte) raster.getDataBuffer()).getData();
			byteBuffer = ByteBuffer.allocate(data.length * Integer.BYTES);
			byteBuffer.put(data);
		}
		break;
		case DataBuffer.TYPE_INT: {
			int[] data = ((DataBufferInt) raster.getDataBuffer()).getData();
			byteBuffer = ByteBuffer.allocate(data.length * Integer.BYTES);
			IntBuffer intBuffer = byteBuffer.asIntBuffer();
			intBuffer.put(data);
		}
		break;
		case DataBuffer.TYPE_SHORT: {
			final short[] data = ((DataBufferShort) raster.getDataBuffer()).getData();
			byteBuffer = ByteBuffer.allocate(data.length * Short.BYTES);
			ShortBuffer shortBuffer = byteBuffer.asShortBuffer();
			shortBuffer.put(data);
		}
		break;
		case DataBuffer.TYPE_USHORT: {
			final short[] data = ((DataBufferUShort) raster.getDataBuffer()).getData();
			byteBuffer = ByteBuffer.allocate(data.length * Short.BYTES);
			ShortBuffer shortBuffer = byteBuffer.asShortBuffer();
			shortBuffer.put(data);
		}
		break;
		case DataBuffer.TYPE_FLOAT: {
			final float[] data = ((DataBufferFloat) raster.getDataBuffer()).getData();
			byteBuffer = ByteBuffer.allocate(data.length * Float.BYTES);
			FloatBuffer floatBuffer = byteBuffer.asFloatBuffer();
			floatBuffer.put(data);
		}
		break;
		case DataBuffer.TYPE_DOUBLE: {
			final double[] data = ((DataBufferDouble) raster.getDataBuffer()).getData();
			byteBuffer = ByteBuffer.allocate(data.length * Double.BYTES);
			DoubleBuffer doubleBuffer = byteBuffer.asDoubleBuffer();
			doubleBuffer.put(data);
		}
		break;
		case DataBuffer.TYPE_UNDEFINED:
		default:
			log.error("Unable to generate image path");
			break;
		}

		return byteBuffer;
	}

	/**
	 * Creates a hash from bytes
	 *
	 * @param bytes
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	private static String createHash(byte[] bytes) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA1");
		byte[] digest = md.digest(bytes);
		String hash = DigestUtils.sha1Hex(digest);
		return hash;
	}

	/**
	 * Extracts the audio data from an MP3 file, removing all ID3 tags
	 *
	 * @param file
	 * @return
	 * @throws Exception
	 */
	private static byte[] getAudioData(File file) throws Exception {

		MP3File mp3File = (MP3File) AudioFileIO.read(file);
		long mp3StartByte = mp3File.getMP3StartByte(file);
		long fileLength = file.length();

		if (fileLength > Integer.MAX_VALUE) {
			// Cowardly refuse to handle audio files bigger than max int
			throw new RuntimeException("File length was too big: " + fileLength);
		}

		long audioStart = mp3StartByte;
		long audioEnd = fileLength;

		if (mp3File.hasID3v1Tag()) {
			int size = mp3File.getID3v1Tag().getSize();
			audioEnd -= size;
		}
		int audioLength = (int) (audioEnd - audioStart);

		log.trace("file length: " + fileLength);
		log.trace("start: " + audioStart);
		log.trace("end: " + audioEnd);
		log.trace("audioLength: " + audioLength);

		FileInputStream fin = new FileInputStream(file);
		long remaining = audioStart;
		while (remaining != 0) {
			long skipped = fin.skip(audioStart);
			remaining -= skipped;
		}
		BoundedInputStream audioIn = new BoundedInputStream(fin, audioLength);
		byte[] bytes = IOUtils.toByteArray(audioIn);

		return bytes;
	}

	public static void main(String[] args) throws Exception {
		//		LibraryMetaData libMetaData = new LibraryMetaData();
		//		TrackUploaderService uploader = new TrackUploaderService();
		//		LibraryIndexerTask task = new LibraryIndexerTask(args[0], libMetaData, uploader);
		//		task.start();
	}
}
