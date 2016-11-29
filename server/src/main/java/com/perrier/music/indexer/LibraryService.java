package com.perrier.music.indexer;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.perrier.music.api.TrackMetaData;
import com.perrier.music.db.IDatabase;
import com.perrier.music.entity.album.Album;
import com.perrier.music.entity.album.AlbumCreateQuery;
import com.perrier.music.entity.album.AlbumFindByNameAndArtistIdQuery;
import com.perrier.music.entity.album.AlbumProvider;
import com.perrier.music.entity.artist.Artist;
import com.perrier.music.entity.artist.ArtistCreateQuery;
import com.perrier.music.entity.artist.ArtistFindByNameQuery;
import com.perrier.music.entity.artist.ArtistProvider;
import com.perrier.music.entity.genre.Genre;
import com.perrier.music.entity.genre.GenreCreateQuery;
import com.perrier.music.entity.genre.GenreFindByNameQuery;
import com.perrier.music.entity.genre.GenreProvider;
import com.perrier.music.entity.track.Track;
import com.perrier.music.entity.track.TrackCreateQuery;
import com.perrier.music.entity.track.TrackFindByIdQuery;
import com.perrier.music.entity.track.TrackFindByNameAndArtistIdAndAlbumIdQuery;
import com.perrier.music.entity.track.TrackUpdateQuery;

/**
 * Responsible for adding, removing, and changing tracks and related entities in the database
 */
public class LibraryService {

	private static final Logger log = LoggerFactory.getLogger(LibraryService.class);

	private final IDatabase db;
	private final GenreProvider genreProvider;
	private final AlbumProvider albumProvider;
	private final ArtistProvider artistProvider;

	@Inject
	public LibraryService(IDatabase db, GenreProvider genreProvider,
			AlbumProvider albumProvider, ArtistProvider artistProvider) {
		this.db = db;
		this.genreProvider = genreProvider;
		this.albumProvider = albumProvider;
		this.artistProvider = artistProvider;
	}

	public void updateTrack(TrackMetaData metaData) {

		final Track originalTrack = this.db.find(new TrackFindByIdQuery(metaData.getId()));

		if (originalTrack.getEdited()) {
			// if the track was edited by the user, don't change it
			log.debug("Track changed on disk but is marked as 'edited'. Changes will not be applied, track: {}",
					originalTrack);
			return;
		}

		// The tag meta-data may have changed
		try {
			final Artist artist = this.addArtist(metaData.getArtist());
			final Artist albumArtist = this.addAlbumArtist(metaData.getAlbumArtist(), artist);
			final Genre genre = this.addGenre(metaData.getGenre());
			final Album album = this.addAlbum(albumArtist, metaData.getAlbum(), metaData.getYear(),
					metaData.getCoverHash(), metaData.getCoverStorageKey(), metaData.getCoverUrl());
			final Track updatedTrack = this.updateTrack(originalTrack, artist, album, genre, metaData.getName(),
					metaData.getNumber(), metaData.getLength(), metaData.getCoverHash(), metaData.getCoverStorageKey(),
					metaData.getCoverUrl(), metaData.getFileModificationDate());

			if (updatedTrack != null) {
				log.info("Track updated: {}", updatedTrack);
			}

		} catch (Exception e) {
			log.error("Unable to update track: {}", metaData, e);
		}
	}

	/*
	public void deleteTrack(MissingTrackEvent event) {
		try {
			this.db.beginTransaction();

			// delete tracks from playlist
			// NOTE: keeping playlist, even if it has no tracks as a result
			this.db.delete(new PlaylistTrackDeleteQuery(track.getId()));

			// delete the track
			this.db.delete(new TrackDeleteQuery(track));

			// delete genre if they have no more tracks
			if (track.getGenre() != null) {
				boolean genreDeleted = genreProvider.deleteIfOrphaned(track.getGenre());
				if (genreDeleted) {
					log.info("Genre removed: {}", track.getGenre());
				}
			}

			// delete album if no more tracks associated with it
			if (track.getAlbum() != null) {
				boolean albumDeleted = albumProvider.deleteIfOrphaned(track.getAlbum());
				if (albumDeleted) {
					log.info("Album removed: {}", track.getAlbum());
				}
			}

			// delete artist if they have no more tracks
			if (track.getArtist() != null) {
				boolean artistDeleted = artistProvider.deleteIfOrphaned(track.getArtist());
				if (artistDeleted) {
					log.info("Artist removed: {}", track.getArtist());
				}
			}

			// TODO: delete cover art if no more associations to it

			this.db.commit();

			log.info("Track removed: {}", track);
		} catch (Exception e) {
			log.error("Unable to handle missing track event: {}", event, e);
		} finally {
			this.db.endTransaction();
		}
	}
	*/

	public Track addTrack(TrackMetaData metaData) {
		try {
			final Artist artist = this.addArtist(metaData.getArtist());
			final Artist albumArtist = this.addAlbumArtist(metaData.getAlbumArtist(), artist);
			final Album album = this.addAlbum(albumArtist, metaData.getAlbum(), metaData.getYear(), metaData.getCoverHash(),
					metaData.getCoverStorageKey(), metaData.getCoverUrl());
			final Genre genre = this.addGenre(metaData.getGenre());
			final Track track = this
					.addTrack(artist, album, genre, metaData.getName(), metaData.getYear(), metaData.getNumber(),
							metaData.getLength(),
							metaData.getAudioHash(), metaData.getAudioStorageKey(), metaData.getAudioUrl(),
							metaData.getCoverHash(), metaData.getCoverStorageKey(), metaData.getCoverUrl(),
							metaData.getFileModificationDate());

			if (track != null) {
				log.info("Track added: {}", track);
			}

			return track;

		} catch (Exception e) {
			log.error("Unable to add new track: {}", metaData, e);
			return null;
		}
	}

	/**
	 * The album artist is set to the track artist if it doesn't exist
	 * <p>
	 * Only add a new artist if it differs from the track artist
	 *
	 * @param albumArtistName
	 * @param trackArtist
	 * @return
	 */
	private Artist addAlbumArtist(String albumArtistName, Artist trackArtist) {
		Artist albumArtist = trackArtist;

		if (!StringUtils.isBlank(albumArtistName)) {
			// we have an album artist. If it is not the same as the track artist, then add it
			if (trackArtist == null || !StringUtils.equalsIgnoreCase(albumArtistName, trackArtist.getName())) {
				albumArtist = this.addArtist(albumArtistName);
			}
		}

		return albumArtist;
	}

	private Genre addGenre(String rawName) {
		if (StringUtils.isBlank(rawName)) {
			return null;
		}

		final String name = StringUtils.trim(rawName);

		try {
			Genre genre = this.db.find(new GenreFindByNameQuery(name));
			if (genre == null) {
				genre = new Genre();
				genre.setName(name);
				this.db.create(new GenreCreateQuery(genre));
			}

			return genre;
		} catch (Exception e) {
			log.error("Error adding genre, name: {}", name, e);
		}

		return null;
	}

	private Track updateTrack(Track track, Artist artist, Album album, Genre genre, String rawName, Integer number,
			Long length, String coverHash, String coverKey, String coverUrl, Long fileModDate) {
		// track name MUST have a value
		if (StringUtils.isBlank(rawName)) {
			log.error("Cannot update track: name was blank");
			return null;
		}

		final String name = StringUtils.trim(rawName);

		try {
			track.setArtist(artist);
			track.setAlbum(album);
			track.setGenre(genre);
			track.setName(name);
			track.setNumber(number);
			track.setLength(length);
			track.setFileModificationDate(new Date(fileModDate));

			// Use album's covert art, otherwise use track's
			if (album != null && album.getCoverHash() != null) {
				track.setCoverHash(album.getCoverHash());
				track.setCoverStorageKey(album.getCoverStorageKey());
				track.setCoverUrl(album.getCoverUrl());
			} else if (coverHash != null) {
				track.setCoverHash(coverHash);
				track.setCoverStorageKey(coverKey);
				track.setCoverUrl(coverUrl);
			}

			this.db.update(new TrackUpdateQuery(track));

			return track;

		} catch (Exception e) {
			log.error("Error updating track, track: {}", track, e);
		}

		return null;
	}

	private Track addTrack(Artist artist, Album album, Genre genre, String rawName, String year, Integer number,
			Long length,
			String audioHash, String audioKey, String audioUrl,
			String coverHash, String coverKey, String coverUrl,
			Long fileModDate) {
		// track name MUST have a value
		if (StringUtils.isBlank(rawName)) {
			log.error("Cannot add track: name was blank, name={}", rawName);
			return null;
		}

		final String name = StringUtils.trim(rawName);

		try {
			Track track = null;
			if (artist != null && album != null) {
				track = this.db.find(new TrackFindByNameAndArtistIdAndAlbumIdQuery(name, artist.getId(), album.getId()));
			}

			if (track == null) {
				track = new Track();
				track.setArtist(artist);
				track.setAlbum(album);
				track.setGenre(genre);
				track.setName(name);
				track.setYear(year);
				track.setNumber(number);
				track.setLength(length);
				track.setFileModificationDate(new Date(fileModDate));
				track.setAudioHash(audioHash);
				track.setAudioStorageKey(audioKey);
				track.setAudioUrl(audioUrl);

				// Use album's covert art, otherwise use tracks
				if (album != null && album.getCoverHash() != null) {
					track.setCoverHash(album.getCoverHash());
					track.setCoverStorageKey(album.getCoverStorageKey());
					track.setCoverUrl(album.getCoverUrl());
				} else if (coverHash != null) {
					track.setCoverHash(coverHash);
					track.setCoverStorageKey(coverKey);
					track.setCoverUrl(coverUrl);
				}

				this.db.create(new TrackCreateQuery(track));
			}

			return track;

		} catch (Exception e) {
			log.error("Error adding track, name: {}", name, e);
		}

		return null;
	}

	private Album addAlbum(Artist artist, String rawName, String year, String coverHash, String coverKey,
			String coverUrl) {
		if (StringUtils.isBlank(rawName)) {
			return null;
		}

		// Don't create an album if it has no associated artist.
		// The album name is not enough to uniquely identify it.
		if (artist == null) {
			log.debug("Skipping adding album '{}': artist was null", rawName);
			return null;
		}

		final String name = StringUtils.trim(rawName);

		try {
			Album album = this.db.find(new AlbumFindByNameAndArtistIdQuery(name, artist.getId()));

			if (album == null) {
				album = new Album();
				album.setArtist(artist);
				album.setName(name);
				album.setYear(year);

				if (coverHash != null) {
					album.setCoverHash(coverHash);
					album.setCoverStorageKey(coverKey);
					album.setCoverUrl(coverUrl);
				}

				this.db.create(new AlbumCreateQuery(album));
			}

			return album;
		} catch (Exception e) {
			log.error("Error adding album, name: {}", name, e);
		}

		return null;
	}

	private Artist addArtist(String rawName) {
		if (StringUtils.isBlank(rawName)) {
			return null;
		}

		final String name = StringUtils.trim(rawName);

		try {
			Artist artist = this.db.find(new ArtistFindByNameQuery(name));
			// new artist
			if (artist == null) {
				artist = new Artist();
				artist.setName(name);
				this.db.create(new ArtistCreateQuery(artist));
				log.debug("Added artist: {}", artist);
			}

			return artist;
		} catch (Exception e) {
			log.error("Error adding artist, name: {}", name, e);
		}

		return null;
	}
}