package com.perrier.music.entity.track;

import static org.apache.commons.lang3.StringUtils.isBlank;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.perrier.music.db.DBException;
import com.perrier.music.entity.album.Album;
import com.perrier.music.entity.album.AlbumCreateQuery;
import com.perrier.music.entity.album.AlbumFindByNameAndArtistIdQuery;
import com.perrier.music.entity.album.AlbumUpdateQuery;
import com.perrier.music.entity.artist.Artist;
import com.perrier.music.entity.track.AbstractTrackUpdater.UpdateResult.Change;

/**
 * TODO: Creating a new album should associate the cover art with the album
 * <p>
 * (i.e., various artists or artist for track is not the same as the album artist)
 */
public class TrackAlbumUpdater extends AbstractTrackUpdater<Album> {

	private static final Logger log = LoggerFactory.getLogger(TrackArtistUpdater.class);

	private final UpdateResult<Artist> albumArtistUpdate;
	private final UpdateResult<Artist> artistUpdate;

	@AssistedInject
	public TrackAlbumUpdater(@Assisted Track track, @Assisted("albumArtist") UpdateResult<Artist> albumArtistUpdate,
	                         @Assisted("artist") UpdateResult<Artist> artistUpdate) {
		super(track);
		this.albumArtistUpdate = albumArtistUpdate;
		this.artistUpdate = artistUpdate;
	}

	public UpdateResult<Album> handleUpdate(String albumNameInput) throws DBException {
		String albumName = normalizeInput(albumNameInput);
		Album trackAlbum = track.getAlbum();
		Change change = determineChange(albumName, trackAlbum);
		UpdateResult<Album> updateResult = doChange(albumName, trackAlbum, change);

		return updateResult;
	}

	private Change determineChange(String albumName, Album trackAlbum) {
		Change change = Change.NONE;

		// original album IS NULL
		if (trackAlbum == null) {
			// updated album HAS VALUE
			if (!isBlank(albumName)) {
				// albumArtist HAS VALUE
				if (this.albumArtistUpdate.getUpdate() != null) {
					// PINK --> CREATE /W ALBUM ARTIST
					change = Change.CREATED;
				}
				// albumArtist IS NULL
				// artist HAS VALUE
				else if (this.artistUpdate.getUpdate() != null) {
					// TURQUOISE --> CREATE W/ ARTIST
					change = Change.CREATED;
				}
				// artist IS NULL
				else {
					// ORANGE --> CREATE WO/ ARTIST
					change = Change.CREATED;
				}
			} else {
				// PURPLE --> NO ACTION
				// album name is empty:
				change = Change.NONE;
			}
			// original album HAS VALUE
			// updated album IS NULL --> DELETE
		} else if (isBlank(albumName)) {
			// GREEN --> DELETE
			log.debug("Removing album from track, albumName={}, track={}", trackAlbum.getName(), track);
			change = Change.DELETED;
			// original album HAS VALUE
			// updated album HAS VALUE
		} else {

			if (this.albumArtistUpdate.isCreated()) {
				// WHITE --> CREATE W/ ALBUM ARTIST
				change = Change.CREATED;
				// album artist was deleted
			} else if (this.albumArtistUpdate.isDeleted()) {
				// Now check if we should create with the Artist or not (only care that the track has an artist)
				if (this.track.getArtist() != null) {
					// BROWN --> CREATE W/ ARTIST
					change = Change.CREATED;
				} else {
					// BLUE --> CREATE WO/ ARTIST
					change = Change.CREATED;
				}
			} else {
				// Check if the album has changed
				if (!trackAlbum.getName().equals(albumName)) {
					// check for superficial changes
					if (!trackAlbum.getName().equalsIgnoreCase(albumName)) {
						// LIGHT BLUE --> CREATE W/ ALBUM-ARTIST
						change = Change.CREATED;
					} else {
						change = Change.UPDATED;
					}
				} else {
					// RED --> NO ACTION
					change = Change.NONE;
				}
			}
		}

		return change;
	}

	private UpdateResult<Album> doChange(String albumName, Album trackAlbum, Change change) throws DBException {
		Album originalAlbum = Album.copy(trackAlbum);
		Album albumUpdate = trackAlbum;

		switch (change) {
			case CREATED:
				Artist artist = null;
				if (albumArtistUpdate.getUpdate() != null) {
					artist = albumArtistUpdate.getUpdate();
				} else if (artistUpdate.getUpdate() != null) {
					// Use track artist as fallback if no album artist
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

		return new UpdateResult<>(originalAlbum, albumUpdate, change);
	}
}
