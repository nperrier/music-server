package com.perrier.music.entity.update;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.perrier.music.db.DBException;
import com.perrier.music.db.IDatabase;
import com.perrier.music.dto.album.AlbumUpdateDto;
import com.perrier.music.entity.album.Album;
import com.perrier.music.entity.album.AlbumDeleteQuery;
import com.perrier.music.entity.album.AlbumFindByNameAndArtistIdQuery;
import com.perrier.music.entity.album.AlbumUpdateQuery;
import com.perrier.music.entity.artist.Artist;
import com.perrier.music.entity.artist.ArtistCreateQuery;
import com.perrier.music.entity.artist.ArtistFindByNameQuery;
import com.perrier.music.entity.artist.ArtistUpdateQuery;
import com.perrier.music.entity.update.UpdateResult.Change;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.normalizeSpace;

// TODO: log all changes
public class AlbumUpdater {

	private static final Logger log = LoggerFactory.getLogger(AlbumUpdater.class);

	private IDatabase db;

	@Inject
	public void setDatabase(IDatabase db) {
		this.db = db;
	}

	public Album handleUpdates(Album origAlbum, AlbumUpdateDto albumUpdateDto) throws DBException {

		String albumName = normalizeSpace(albumUpdateDto.getName());
		Change albumNameChange = determineAlbumNameChange(origAlbum, albumName);

		String artistNameUpdate = normalizeSpace(albumUpdateDto.getArtist());
		Change artistChange = determineArtistNameChange(origAlbum, origAlbum.getArtist(), artistNameUpdate);

		switch (albumNameChange) {
		case NONE:
		case UPDATED:
		case CREATED: {
			// the album name changed, so check if an album for the artist already exists
			handleChange(origAlbum, albumNameChange, albumName, artistChange, artistNameUpdate);
			break;
		}
		case DELETED: {
			// If we removed the album name, then we can ignore any change to the artist as the album is toast
			try {
				this.db.beginTransaction();
				// Remove Track associations
				origAlbum.getTracks().clear();
				// Delete Album
				this.db.delete(new AlbumDeleteQuery(origAlbum));
				// TODO: Delete Artist if orphaned
				this.db.commit();
			} finally {
				this.db.endTransaction();
			}
			break;
		}
		default:
			throw new RuntimeException("Unknown change: " + albumNameChange);
		}

		return null;
	}

	private void handleChange(Album origAlbum, Change albumNameChange, String albumNameUpdate,
			Change artistChange, String artistNameUpdate) {

		switch (artistChange) {
		case CREATED: {
			// The album artist has changed or has been newly added
			// Check if the Artist exists already
			Artist artistUpdate = this.db.find(new ArtistFindByNameQuery(artistNameUpdate));
			if (artistUpdate == null) {
				artistUpdate = new Artist();
				artistUpdate.setName(artistNameUpdate);
				artistUpdate = this.db.create(new ArtistCreateQuery(artistUpdate));
			}
			doChange(origAlbum, albumNameUpdate, artistUpdate);
			break;
		}
		case UPDATED: {
			// The artist has only changed superficially
			// Update the artist name
			Artist albumArtist = origAlbum.getArtist();
			albumArtist.setName(artistNameUpdate);
			this.db.update(new ArtistUpdateQuery(albumArtist));
			break;
		}
		case DELETED: {
			// The artist has been removed from the album
			doChange(origAlbum, albumNameUpdate, null);
			break;
		}
		case NONE: {
			// Artist is the same. Album name changed.
			Long artistId = null;
			if (origAlbum.getArtist() != null) {
				artistId = origAlbum.getArtist().getId();
			}
			Album updateAlbum = this.db.find(new AlbumFindByNameAndArtistIdQuery(albumNameUpdate, artistId));

			if (updateAlbum == null || Change.UPDATED.equals(albumNameChange)) {
				// Update the album name
				origAlbum.setName(albumNameUpdate);
				this.db.update(new AlbumUpdateQuery(origAlbum));
			} else if (!origAlbum.getId().equals(updateAlbum.getId())) {
				// The name changed and an album exists for the album/artist
				associateTracksWithNewAlbum(origAlbum, updateAlbum);
			}
			break;
		}
		default:
			throw new RuntimeException("Unknown change: " + artistChange);
		}
	}

	private void doChange(Album origAlbum, String albumNameUpdate, Artist artistUpdate) {
		try {
			this.db.beginTransaction();

			// Check if an Album already exists for the artist:
			Long artistId = null;
			if (artistUpdate != null) {
				artistId = artistUpdate.getId();
			}
			Album updateAlbum = this.db.find(new AlbumFindByNameAndArtistIdQuery(albumNameUpdate, artistId));

			if (updateAlbum == null) {
				// No album exists. Update the artist for the original album
				origAlbum.setArtist(artistUpdate);
				this.db.update(new AlbumUpdateQuery(origAlbum));
			} else if (!updateAlbum.getId().equals(origAlbum.getId())) {
				// An album exists, so associate original album's tracks to the existing album
				associateTracksWithNewAlbum(origAlbum, updateAlbum);
				// TODO: Delete the Artist if it's orphaned
			}

			this.db.commit();
		} finally {
			this.db.endTransaction();
		}
	}

	/**
	 * An album exists, so associate original album's tracks to the existing album
	 *
	 * @param origAlbum
	 * @param updateAlbum
	 */
	private void associateTracksWithNewAlbum(Album origAlbum, Album updateAlbum) {
		try {
			this.db.beginTransaction();

			// Associate all tracks from old album to new one
			updateAlbum.getTracks().addAll(origAlbum.getTracks());
			// Remove Track associations
			origAlbum.getTracks().clear();
			// Update db
			this.db.update(new AlbumUpdateQuery(origAlbum));
			this.db.update(new AlbumUpdateQuery(updateAlbum));

			// Delete the old album
			this.db.delete(new AlbumDeleteQuery(origAlbum));

			this.db.commit();
		} finally {
			this.db.endTransaction();
		}
	}

	/**
	 * Determine the change to the album name
	 *
	 * @param album
	 * @param albumName
	 * @return
	 */
	private Change determineAlbumNameChange(Album album, String albumName) {
		Change change = Change.NONE;

		if (isBlank(albumName)) {
			log.debug("Deleting album, albumName={}, album={}", album.getName(), album);
			change = Change.DELETED;
		} else if (!album.getName().equals(albumName)) {
			// Checks if changes are superficial (e.g., capitalization)
			if (album.getName().equalsIgnoreCase(albumName)) {
				log.debug("Updating name for album, newAlbumName={}, album={}", albumName, album);
				change = Change.UPDATED;
			} else {
				log.debug("Changing name for album, newAlbumName={}, track={}", albumName, album);
				change = Change.CREATED;
			}
		}

		return change;
	}

	private Change determineArtistNameChange(Album album, Artist albumArtist, String artistName) {
		Change change = Change.NONE;

		if (albumArtist == null) {
			if (!isBlank(artistName)) {
				log.debug("Adding new artist to album, artistName={}, album={}", artistName, album);
				change = Change.CREATED;
			}
		} else if (isBlank(artistName)) {
			log.debug("Removing artist from album, artistName={}, album={}", albumArtist.getName(), album);
			change = Change.DELETED;
		} else if (!albumArtist.getName().equals(artistName)) {
			// Checks if changes are superficial (e.g., capitalization)
			if (albumArtist.getName().equalsIgnoreCase(artistName)) {
				log.debug("Updating artist for album, newArtistName={}, album={}", artistName, album);
				change = Change.UPDATED;
			} else {
				log.debug("Changing artist for album, newArtistName={}, album={}", artistName, album);
				change = Change.CREATED;
			}
		}

		return change;
	}
}
