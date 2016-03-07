package com.perrier.music.indexer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Date;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.AbstractIdleService;
import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.perrier.music.coverart.CoverArtException;
import com.perrier.music.coverart.ICoverArtService;
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
import com.perrier.music.entity.library.Library;
import com.perrier.music.entity.playlist.PlaylistTrackDeleteQuery;
import com.perrier.music.entity.track.*;
import com.perrier.music.indexer.event.ChangedTrackEvent;
import com.perrier.music.indexer.event.MissingTrackEvent;
import com.perrier.music.indexer.event.UnknownTrackEvent;
import com.perrier.music.tag.ITag;
import com.perrier.music.tag.TagFactory;

/**
 * Responsible for adding, removing, and changing tracks and related entities in the database
 */
public class LibraryService extends AbstractIdleService implements ILibraryService {

	private static final Logger log = LoggerFactory.getLogger(LibraryService.class);

	private final IDatabase db;
	private final EventBus bus;
	private final ICoverArtService coverArtService;
	private final GenreProvider genreProvider;
	private final AlbumProvider albumProvider;
	private final ArtistProvider artistProvider;

	@Inject
	public LibraryService(IDatabase db, EventBus bus, ICoverArtService coverArtService, GenreProvider genreProvider,
	                      AlbumProvider albumProvider, ArtistProvider artistProvider) {
		this.db = db;
		this.bus = bus;
		this.coverArtService = coverArtService;
		this.genreProvider = genreProvider;
		this.albumProvider = albumProvider;
		this.artistProvider = artistProvider;
	}

	@Override
	protected void startUp() throws Exception {
		this.bus.register(this);
	}

	@Override
	protected void shutDown() throws Exception {
		this.bus.unregister(this);
	}

	@Subscribe
	@AllowConcurrentEvents
	public void handle(ChangedTrackEvent event) {
		log.debug(event.toString());

		final Track originalTrack = event.getTrack();
		if (originalTrack.getEdited()) {
			// if the track was edited by the user, don't change it
			log.debug("Track changed on disk but is marked as 'edited'. Changes will not be applied, track: {}",
					originalTrack);
			return;
		}

		// The tag meta-data may have changed
		try {
			File file = event.getFile();
			ITag tag = TagFactory.parseTag(file);
			log.debug("Tag: {}", tag);

			final Artist artist = this.addArtist(tag.getArtist());
			final Artist albumArtist = this.addAlbumArtist(tag.getAlbumArtist(), artist);
			final Genre genre = this.addGenre(tag.getGenre());
			final Album album = this.addAlbum(albumArtist, tag.getAlbum(), tag.getYear(), tag.getCoverArt());
			final Track updatedTrack = this.updateTrack(originalTrack, artist, album, genre, tag.getTrack(), tag.getNumber(),
					tag.getLength(), tag.getCoverArt(), file, event.getLibrary());

			if (updatedTrack != null) {
				log.info("Track updated: {}", updatedTrack);
			}

		} catch (Exception e) {
			log.error("Unable to handle changed track event: {}", event, e);
		}

	}

	@Subscribe
	@AllowConcurrentEvents
	public void handle(MissingTrackEvent event) {
		log.debug(event.toString());

		Track track = event.getTrack();
		try {
			this.db.beginTransaction();

			// delete tracks from playlist
			// NOTE: keeping playlist, even though it will be empty
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

	@Subscribe
	@AllowConcurrentEvents
	public void handle(UnknownTrackEvent event) {
		log.debug(event.toString());

		try {
			File file = event.getFile();
			ITag tag = TagFactory.parseTag(file);

			log.debug("Tag: {}", tag);

			final Artist artist = this.addArtist(tag.getArtist());
			final Artist albumArtist = this.addAlbumArtist(tag.getAlbumArtist(), artist);
			final Genre genre = this.addGenre(tag.getGenre());
			final Album album = this.addAlbum(albumArtist, tag.getAlbum(), tag.getYear(), tag.getCoverArt());
			final Track track = this.addTrack(artist, album, genre, tag.getTrack(), tag.getYear(), tag.getNumber(), tag.getLength(), tag
					.getCoverArt(), file, event.getLibrary());

			if (track != null) {
				log.info("Track added: {}", track);
			}

		} catch (Exception e) {
			log.error("Unable to handle unknown track event: {}", event, e);
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

	private String addCoverArt(BufferedImage image) {
		String coverArt = null;

		try {
			coverArt = this.coverArtService.cacheCoverArt(image);
		} catch (CoverArtException e) {
			log.error("Could not create cover for image file", e);
		} catch (Exception e) {
			log.error("Error adding coverArt", e);
		}

		return coverArt;
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
	                          Long length, BufferedImage image, File file, Library library) {
		// track name MUST have a value
		if (StringUtils.isBlank(rawName)) {
			log.error("Cannot update track: name was blank, path={}", track.getPath());
			return null;
		}

		final String name = StringUtils.trim(rawName);

		try {
			track.setArtist(artist);
			track.setAlbum(album);
			track.setGenre(genre);
			track.setLibrary(library);
			track.setName(name);
			track.setNumber(number);
			track.setLength(length);
			track.setFileModificationDate(new Date(file.lastModified()));

			// Use album's covert art, otherwise use track's
			if (album != null && album.getCoverArt() != null) {
				track.setCoverArt(album.getCoverArt());
			} else if (image != null) {
				final String coverArt = this.addCoverArt(image);
				track.setCoverArt(coverArt);
			}

			this.db.update(new TrackUpdateQuery(track));

			return track;

		} catch (Exception e) {
			log.error("Error updating track, track: {}, file: {}", track, file, e);
		}

		return null;
	}

	private Track addTrack(Artist artist, Album album, Genre genre, String rawName, String year, Integer number, Long length,
	                       BufferedImage image, File file, Library library) {
		// track name MUST have a value
		if (StringUtils.isBlank(rawName)) {
			log.error("Cannot add track: name was blank, file={}", file);
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
				track.setLibrary(library);
				track.setName(name);
				track.setYear(year);
				track.setNumber(number);
				track.setLength(length);
				track.setFileModificationDate(new Date(file.lastModified()));
				track.setPath(file.getCanonicalPath());

				// Use albums covert art, otherwise use tracks
				if (album != null && album.getCoverArt() != null) {
					track.setCoverArt(album.getCoverArt());
				} else if (image != null) {
					final String coverArt = this.addCoverArt(image);
					track.setCoverArt(coverArt);
				}

				this.db.create(new TrackCreateQuery(track));
			}

			return track;

		} catch (Exception e) {
			log.error("Error adding track, name: {}", name, e);
		}

		return null;
	}

	private Album addAlbum(Artist artist, String rawName, String year, BufferedImage image) {
		if (StringUtils.isBlank(rawName)) {
			return null;
		}

		final String name = StringUtils.trim(rawName);

		try {
			Album album = null;
			if (artist != null) {
				album = this.db.find(new AlbumFindByNameAndArtistIdQuery(name, artist.getId()));
			}

			if (album == null) {
				album = new Album();
				album.setArtist(artist);
				album.setName(name);
				album.setYear(year);

				if (image != null) {
					album.setCoverArt(this.addCoverArt(image));
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
			}

			return artist;
		} catch (Exception e) {
			log.error("Error adding artist, name: {}", name, e);
		}

		return null;
	}
}
