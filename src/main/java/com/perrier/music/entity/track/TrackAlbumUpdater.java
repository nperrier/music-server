package com.perrier.music.entity.track;

import static org.apache.commons.lang3.StringUtils.isBlank;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.perrier.music.db.DBException;
import com.perrier.music.entity.album.Album;
import com.perrier.music.entity.album.AlbumCreateQuery;
import com.perrier.music.entity.album.AlbumFindByNameAndArtistIdQuery;
import com.perrier.music.entity.album.AlbumUpdateQuery;
import com.perrier.music.entity.artist.Artist;
import com.perrier.music.entity.track.AbstractTrackUpdater.UpdateResult.Change;

/**
 * TODO: Creating a new album should associate the cover art with the album
 * 
 * TODO: Need to figure out how to handle changes when tracks album artist != track artist
 * 
 * (i.e., various artists or artist for track is not the same as the album artist)
 */
public class TrackAlbumUpdater extends AbstractTrackUpdater<Album> {

	private static final Logger log = LoggerFactory.getLogger(TrackArtistUpdater.class);

	private final UpdateResult<Artist> artistUpdate;

	@AssistedInject
	public TrackAlbumUpdater(@Assisted Track track, @Assisted UpdateResult<Artist> artistUpdate) {
		super(track);
		this.artistUpdate = artistUpdate;
	}

	public UpdateResult<Album> handleUpdate(String albumNameInput) throws DBException {
		String albumName = normalizeInput(albumNameInput);
		Album trackAlbum = track.getAlbum();
		Change change = determineChange(albumName, trackAlbum);
		UpdateResult updateResult = doChange(albumName, trackAlbum, change);

		return updateResult;
	}

	private Change determineChange(String albumName, Album trackAlbum) {
		Change change = Change.NONE;
		boolean artistChanged = artistUpdate.isCreatedOrDeleted();

		if (trackAlbum == null) {
			if (!isBlank(albumName)) {
				log.debug("Added new album to track, albumName={}, trackAlbum={}", albumName, track);
				change = Change.CREATED;
			}
		} else if (isBlank(albumName)) {
			log.debug("Removing album from track, albumName={}, track={}", trackAlbum.getName(), track);
			change = Change.DELETED;
		} else if (!trackAlbum.getName().equals(albumName)) {
			// Check if changes are superficial (e.g., capitalization)
			if (trackAlbum.getName().equalsIgnoreCase(albumName) && !artistChanged) {
				log.debug("Updating album for track, newAlbumName={}, track={}", albumName, track);
				change = Change.UPDATED;
			}
			// But if the artist changed or the changes were not superficial, we will have to create a new album
			else {
				log.debug("Changing album for track, newAlbumName={}, track={}", albumName, track);
				change = Change.CREATED;
			}
		} else if (artistChanged) {
			// the album wasn't changed, but the artist might have, so we have to change the album
			// to associate it with the new artist value
			log.debug("Album didn't change, but the artist did.  Changing the album artist, artist={}, track={}",
					artistUpdate, track);
			change = Change.CREATED;
		}

		return change;
	}

	private UpdateResult doChange(String albumName, Album trackAlbum, Change change) throws DBException {
		Album originalAlbum = Album.copy(trackAlbum);
		Album albumUpdate = trackAlbum;

		switch (change) {
		case CREATED:
			Artist artist = track.getArtist();

			if (artistUpdate.isCreatedOrDeleted()) {
				artist = artistUpdate.getUpdate();
			}

			if (artist != null) {
				albumUpdate = this.db.find(new AlbumFindByNameAndArtistIdQuery(albumName, artist.getId()));
			} else {
				albumUpdate = this.db.find(new AlbumFindByNameAndArtistIdQuery(albumName));
			}

			if (albumUpdate == null) {
				albumUpdate = new Album();
				albumUpdate.setName(albumName);
				albumUpdate.setArtist(artist);
				albumUpdate = this.db.create(new AlbumCreateQuery(albumUpdate));
			}
			break;
		case UPDATED:
			albumUpdate.setName(albumName);
			albumUpdate = this.db.update(new AlbumUpdateQuery(albumUpdate));
			break;
		case DELETED:
			albumUpdate = null;
			break;
		case NONE:
			// noop
			break;
		default:
			throw new RuntimeException("Unknown change type: " + change);
		}

		return new UpdateResult<Album>(originalAlbum, albumUpdate, change);
	}
}
