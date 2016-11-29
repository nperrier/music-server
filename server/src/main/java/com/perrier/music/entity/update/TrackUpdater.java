package com.perrier.music.entity.update;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.perrier.music.api.TrackUpdateDto;
import com.perrier.music.db.DBException;
import com.perrier.music.db.IDatabase;
import com.perrier.music.entity.album.Album;
import com.perrier.music.entity.album.AlbumProvider;
import com.perrier.music.entity.artist.Artist;
import com.perrier.music.entity.artist.ArtistProvider;
import com.perrier.music.entity.genre.Genre;
import com.perrier.music.entity.genre.GenreProvider;
import com.perrier.music.entity.track.Track;
import com.perrier.music.entity.track.TrackUpdateQuery;

public class TrackUpdater {

	private static final Logger log = LoggerFactory.getLogger(TrackUpdater.class);

	private IDatabase db;

	private TrackArtistUpdater trackArtistUpdater;
	private TrackAlbumUpdater trackAlbumUpdater;
	private TrackGenreUpdater trackGenreUpdater;

	private ArtistProvider artistProvider;
	private AlbumProvider albumProvider;
	private GenreProvider genreProvider;

	@Inject
	public void setDatabase(IDatabase db) {
		this.db = db;
	}

	@Inject
	public void setTrackArtistUpdater(TrackArtistUpdater trackArtistUpdater) {
		this.trackArtistUpdater = trackArtistUpdater;
	}

	@Inject
	public void setTrackAlbumUpdater(TrackAlbumUpdater trackAlbumUpdater) {
		this.trackAlbumUpdater = trackAlbumUpdater;
	}

	@Inject
	public void setTrackGenreUpdater(TrackGenreUpdater trackGenreUpdater) {
		this.trackGenreUpdater = trackGenreUpdater;
	}

	@Inject
	public void setArtistProvider(ArtistProvider artistProvider) {
		this.artistProvider = artistProvider;
	}

	@Inject
	public void setAlbumProvider(AlbumProvider albumProvider) {
		this.albumProvider = albumProvider;
	}

	@Inject
	public void setGenreProvider(GenreProvider genreProvider) {
		this.genreProvider = genreProvider;
	}

	public Track handleUpdates(Track track, TrackUpdateDto trackUpdateDto) throws DBException {

		UpdateResult<Artist> artist = trackArtistUpdater.handleUpdate(track, trackUpdateDto.getArtist());
		UpdateResult<Artist> albumArtist = trackArtistUpdater.handleUpdate(track, trackUpdateDto.getAlbumArtist());
		UpdateResult<Album> album = trackAlbumUpdater.handleUpdate(track, albumArtist, artist, trackUpdateDto.getAlbum());
		UpdateResult<Genre> genre = trackGenreUpdater.handleUpdate(track, trackUpdateDto.getGenre());

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

		// TODO: coverArt changes

		boolean trackChanged = (artist.isCreatedOrDeleted() || album.isCreatedOrDeleted() || genre.isCreatedOrDeleted()
				|| nameChanged || yearChanged || numberChanged);

		if (trackChanged) {
			if (artist.isCreatedOrDeleted()) {
				track.setArtist(artist.getUpdate());
			}
			if (album.isCreatedOrDeleted()) {
				track.setAlbum(album.getUpdate());
			}
			if (genre.isCreatedOrDeleted()) {
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

			try {
				// if the entity can't be deleted, log an error, don't bubble up
				cleanupOrphans(artist, album, genre);
			} catch (DBException dbe) {
				log.error("Unable to clean up orphans", dbe);
			}
		}

		return track;
	}

	/**
	 * Editing Track info may have caused previous associations to be removed creating orphaned entities (Genre, Artist,
	 * Album)
	 * <p>
	 * Delete them if there are no more associations to them
	 *
	 * @param artist
	 * @param album
	 * @param genre
	 */
	private void cleanupOrphans(UpdateResult<Artist> artist, UpdateResult<Album> album, UpdateResult<Genre> genre)
			throws DBException {
		removeOrphanedGenres(genre);
		removeOrphanedArtists(artist);
		removeOrphanedAlbums(album);
	}

	private void removeOrphanedAlbums(UpdateResult<Album> album) throws DBException {
		final Album originalAlbum = album.getOriginal();

		if (originalAlbum == null) {
			return;
		}

		// if we created a new album, then the old one might need to be removed
		// if we removed an existing album, then it might not have any more associations
		if (album.getUpdate() == null || !album.getUpdate().getId().equals(originalAlbum.getId())) {
			log.info("Album removed from track, possible orphan: album={}", originalAlbum);
			boolean albumDeleted = albumProvider.deleteIfOrphaned(originalAlbum);
			if (albumDeleted) {
				log.info("Deleted: album={}", originalAlbum);
				// if the album is toast, we should also try to delete the album artist if it's orphaned
				final Artist originalAlbumArtist = originalAlbum.getArtist();
				if (originalAlbumArtist != null) {
					// TODO: also check track-artist? it may already have been deleted or still exist...hmmmm
					log.info("Checking if album-artist is a possible orphan: albumArtist={}", originalAlbumArtist);
					boolean albumArtistDeleted = artistProvider.deleteIfOrphaned(originalAlbumArtist);
					if (albumArtistDeleted) {
						log.info("Deleted: albumArtist={}", originalAlbumArtist);
					}
				}
			}
		}
	}

	private void removeOrphanedArtists(UpdateResult<Artist> artist) throws DBException {
		final Artist originalArtist = artist.getOriginal();

		if (originalArtist == null) {
			return;
		}

		// if we created a new artist, then the old one might need to be removed
		// if we removed an existing artist, then it might not have any more associations
		if (artist.getUpdate() == null || !artist.getUpdate().getId().equals(originalArtist.getId())) {
			log.info("Artist removed from track, possible orphan: artist={}", originalArtist);
			boolean artistDeleted = artistProvider.deleteIfOrphaned(originalArtist);
			if (artistDeleted) {
				log.info("Deleted: artist={}", originalArtist);
			}
		}
	}

	private void removeOrphanedGenres(UpdateResult<Genre> genre) throws DBException {
		final Genre originalGenre = genre.getOriginal();

		if (originalGenre == null) {
			return;
		}

		// if we created a new genre, then the old one might need to be removed
		// if we removed an existing genre, then it might not have any more associations
		if (genre.getUpdate() == null || !genre.getUpdate().getId().equals(originalGenre.getId())) {
			log.info("Genre removed from track, possible orphan: genre={}", originalGenre);
			boolean genreDeleted = genreProvider.deleteIfOrphaned(originalGenre);
			if (genreDeleted) {
				log.info("Deleted: genre={}", originalGenre);
			}
		}
	}
}