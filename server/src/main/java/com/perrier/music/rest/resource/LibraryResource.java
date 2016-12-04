package com.perrier.music.rest.resource;

import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.inject.Inject;
import com.perrier.music.api.LibraryMetaData;
import com.perrier.music.api.TrackMetaData;
import com.perrier.music.db.DBException;
import com.perrier.music.entity.track.Track;
import com.perrier.music.entity.track.TrackProvider;

@Path("api/library")
@Produces(MediaType.APPLICATION_JSON)
public class LibraryResource {

	@Inject
	private TrackProvider trackProvider;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public LibraryMetaData getAll() throws DBException {

		List<Track> allTracks = this.trackProvider.findAll();
		List<TrackMetaData> trackMetaDatas = toTrackMetaDatas(allTracks);
		LibraryMetaData libMetaData = new LibraryMetaData();
		libMetaData.setTrackMetaData(trackMetaDatas);

		return libMetaData;
	}

	private static List<TrackMetaData> toTrackMetaDatas(List<Track> allTracks) {
		List<TrackMetaData> trackMetaDatas = allTracks.stream()
				.map(LibraryResource::toTrackMetaData)
				.collect(Collectors.toList());

		return trackMetaDatas;
	}

	/**
	 * Convert Track -> TrackMetaData
	 *
	 * @param t
	 * @return
	 */
	private static TrackMetaData toTrackMetaData(Track t) {
		TrackMetaData trackMetaData = new TrackMetaData();
		trackMetaData.setName(t.getName());
		trackMetaData.setArtist((t.getArtist() == null ? null : t.getArtist().getName()));
		trackMetaData.setAlbum((t.getAlbum() == null ? null : t.getAlbum().getName()));
		trackMetaData.setAlbumArtist((t.getAlbumArtist() == null ? null : t.getAlbumArtist().getName()));
		trackMetaData.setGenre((t.getGenre() == null ? null : t.getGenre().getName()));
		trackMetaData.setLength(t.getLength());
		trackMetaData.setNumber(t.getNumber());
		trackMetaData.setYear(t.getYear());
		trackMetaData.setAudioHash(t.getAudioHash());
		trackMetaData.setAudioStorageKey(t.getAudioStorageKey());
		trackMetaData.setAudioUrl(t.getAudioUrl());
		trackMetaData.setCoverHash(t.getCoverHash());
		trackMetaData.setCoverStorageKey(t.getCoverStorageKey());
		trackMetaData.setCoverUrl(t.getCoverUrl());
		trackMetaData.setFileModificationDate(t.getFileModificationDate().getTime());
		return trackMetaData;
	}
}
