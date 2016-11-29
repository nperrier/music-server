package com.perrier.music.indexer;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.net.URL;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.perrier.music.api.ServerAPI;
import com.perrier.music.api.TrackMetaData;
import com.perrier.music.storage.S3StorageService;
import com.perrier.music.tag.ITag;

public class TrackUploaderService {

	private static final Logger log = LoggerFactory.getLogger(TrackUploaderService.class);

	private final S3StorageService storageService;
	private final ServerAPI serverAPI;

	public TrackUploaderService(S3StorageService storageService, ServerAPI serverAPI) {
		this.storageService = storageService;
		this.serverAPI = serverAPI;
	}

	/**
	 * TODO: how to handle new track hashes while still scanning?
	 * Update LibraryMetaData map with track? What if uploads happen in parallel?
	 * Don't want to upload cover art for tracks multiple times if they are all the same hash
	 */
	public TrackMetaData uploadTrack(long fileModDate, ITag fileTag, BufferedImage coverImage,
			String coverImageHash, byte[] audioData, String audioDataHash) throws Exception {
		try {
			// upload track to S3
			URL audioUrl = storageService.putAudio(audioDataHash, audioData);

			// upload cover art to S3
			// First, compress the image to a web-friendly, PNG format
			// TODO: scale image to standard size?
			// TODO: Upload multiple sizes?
			ByteArrayOutputStream out = new ByteArrayOutputStream(8 * 1024); // 8kb initial size
			ImageIO.write(coverImage, "PNG", out);
			byte[] bytes = out.toByteArray();
			URL coverUrl = storageService.putCover(coverImageHash, bytes);

			// send to server
			// TODO: determine whether cover is already uploaded - check the hash
			TrackMetaData trackMetaData = new TrackMetaData();
			trackMetaData.setName(fileTag.getTrack());
			trackMetaData.setArtist(fileTag.getArtist());
			trackMetaData.setAlbum(fileTag.getAlbum());
			trackMetaData.setAlbumArtist(fileTag.getAlbumArtist());
			trackMetaData.setGenre(fileTag.getGenre());
			trackMetaData.setLength(fileTag.getLength());
			trackMetaData.setNumber(fileTag.getNumber());
			trackMetaData.setYear(fileTag.getYear());
			trackMetaData.setAudioHash(audioDataHash);
			trackMetaData.setAudioStorageKey(S3StorageService.getAudioKey(audioDataHash));
			trackMetaData.setAudioUrl(audioUrl.toExternalForm());
			trackMetaData.setCoverHash(coverImageHash);
			trackMetaData.setCoverStorageKey(S3StorageService.getCoverKey(coverImageHash));
			trackMetaData.setCoverUrl(coverUrl.toExternalForm());
			trackMetaData.setFileModificationDate(fileModDate);

			// send track meta data to app server:
			TrackMetaData response = this.serverAPI.postTrack(trackMetaData);

			return response;

		} catch (Exception e) {
			log.error("Failed to upload new track, fileTag={}", fileTag, e);
			throw e;
		}
	}

	// TODO
	public void handleChangedTrack() {
		log.warn("Not Yet Implemented, ignoring");
	}
}
