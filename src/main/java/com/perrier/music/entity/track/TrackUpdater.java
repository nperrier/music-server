package com.perrier.music.entity.track;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.perrier.music.db.DBException;
import com.perrier.music.db.IDatabase;
import com.perrier.music.dto.track.TrackUpdateDto;
import com.perrier.music.entity.album.Album;
import com.perrier.music.entity.artist.Artist;
import com.perrier.music.entity.genre.Genre;
import com.perrier.music.entity.track.AbstractTrackUpdater.UpdateResult;

public class TrackUpdater {

	private static final Logger log = LoggerFactory.getLogger(TrackUpdater.class);

	private final Track track;

	private IDatabase db;
	private ITrackArtistUpdaterFactory trackArtistUpdaterFactory;
	private ITrackAlbumUpdaterFactory trackAlbumUpdaterFactory;
	private ITrackGenreUpdaterFactory trackGenreUpdaterFactory;

	@Inject
	public void setDatabase(IDatabase db) {
		this.db = db;
	}

	@Inject
	public void setTrackArtistUpdaterFactory(ITrackArtistUpdaterFactory trackArtistUpdaterFactory) {
		this.trackArtistUpdaterFactory = trackArtistUpdaterFactory;
	}

	@Inject
	public void setTrackAlbumUpdaterFactory(ITrackAlbumUpdaterFactory trackAlbumUpdaterFactory) {
		this.trackAlbumUpdaterFactory = trackAlbumUpdaterFactory;
	}

	@Inject
	public void setTrackAlbumUpdaterFactory(ITrackGenreUpdaterFactory trackGenreUpdaterFactory) {
		this.trackGenreUpdaterFactory = trackGenreUpdaterFactory;
	}

	@AssistedInject
	public TrackUpdater(@Assisted Track track) {
		this.track = track;
	}

	public void handleUpdates(TrackUpdateDto trackUpdateDto) throws DBException {

		TrackArtistUpdater trackArtistUpdater = trackArtistUpdaterFactory.create(track);
		UpdateResult<Artist> artist = trackArtistUpdater.handleUpdate(trackUpdateDto.getArtist());

		TrackAlbumUpdater trackAlbumUpdater = trackAlbumUpdaterFactory.create(track, artist);
		UpdateResult<Album> album = trackAlbumUpdater.handleUpdate(trackUpdateDto.getAlbum());

		TrackGenreUpdater trackGenreUpdater = trackGenreUpdaterFactory.create(track);
		UpdateResult<Genre> genre = trackGenreUpdater.handleUpdate(trackUpdateDto.getGenre());

		boolean nameChanged = false;
		if (!track.getName().equals(trackUpdateDto.getName())) {
			nameChanged = true;
		}

		boolean numberChanged = false;
		if (track.getNumber() == null) {
			if (trackUpdateDto.getNumber() != null) {
				numberChanged = true;
			}
		} else if (!track.getNumber().equals(trackUpdateDto.getNumber())) {
			numberChanged = true;
		}

		boolean yearChanged = false;
		if (track.getYear() == null) {
			if (trackUpdateDto.getYear() != null) {
				yearChanged = true;
			}
		} else if (!track.getYear().equals(trackUpdateDto.getYear())) {
			yearChanged = true;
		}

		boolean trackChanged = (artist.getChanged() || album.getChanged() || genre.getChanged() || nameChanged
				|| yearChanged || numberChanged);

		if (trackChanged) {
			if (artist.getChanged()) {
				track.setArtist(artist.getUpdate());
			}
			if (album.getChanged()) {
				track.setAlbum(album.getUpdate());
			}
			if (genre.getChanged()) {
				track.setGenre(genre.getUpdate());
			}
			if (nameChanged) {
				track.setName(trackUpdateDto.getName());
			}
			if (numberChanged) {
				track.setNumber(trackUpdateDto.getNumber());
			}
			if (yearChanged) {
				track.setYear(trackUpdateDto.getYear());
			}
			// mark track as 'edited' so modifications persist even after re-scanning track
			track.setEdited(true);

			this.db.update(new TrackUpdateQuery(track));
		}

		// TODO: coverArt changes
	}
}
