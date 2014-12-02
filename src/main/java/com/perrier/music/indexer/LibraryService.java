package com.perrier.music.indexer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.AbstractIdleService;
import com.google.inject.Inject;
import com.perrier.music.coverart.CoverArtException;
import com.perrier.music.coverart.ICoverArtService;
import com.perrier.music.db.IDatabase;
import com.perrier.music.entity.album.Album;
import com.perrier.music.entity.album.AlbumCreateQuery;
import com.perrier.music.entity.album.AlbumFindByNameAndArtistIdQuery;
import com.perrier.music.entity.artist.Artist;
import com.perrier.music.entity.artist.ArtistCreateQuery;
import com.perrier.music.entity.artist.ArtistFindByNameQuery;
import com.perrier.music.entity.genre.Genre;
import com.perrier.music.entity.genre.GenreCreateQuery;
import com.perrier.music.entity.genre.GenreFindByNameQuery;
import com.perrier.music.entity.library.Library;
import com.perrier.music.entity.playlist.PlaylistTrackDeleteQuery;
import com.perrier.music.entity.track.Track;
import com.perrier.music.entity.track.TrackCreateQuery;
import com.perrier.music.entity.track.TrackDeleteQuery;
import com.perrier.music.entity.track.TrackFindByNameAndArtistIdAndAlbumIdQuery;
import com.perrier.music.indexer.event.ChangedTrackEvent;
import com.perrier.music.indexer.event.MissingTrackEvent;
import com.perrier.music.indexer.event.UnknownTrackEvent;
import com.perrier.music.tag.ITag;
import com.perrier.music.tag.TagFactory;

public class LibraryService extends AbstractIdleService implements ILibraryService {

	private static final Logger log = LoggerFactory.getLogger(LibraryService.class);

	private final IDatabase db;
	private final EventBus bus;
	private final ICoverArtService coverArtService;

	@Inject
	public LibraryService(IDatabase db, EventBus bus, ICoverArtService coverArtService) {
		this.db = db;
		this.bus = bus;
		this.coverArtService = coverArtService;
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
	public void handle(ChangedTrackEvent event) {
		log.info(event.toString());
	}

	@Subscribe
	public void handle(MissingTrackEvent event) {
		log.debug(event.toString());

		Track track = event.getTrack();
		try {
			this.db.beginTransaction();
			// TODO: What else should be cleaned up?
			this.db.delete(new PlaylistTrackDeleteQuery(track.getId()));
			this.db.delete(new TrackDeleteQuery(track));
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

			Artist artist = this.addArtist(tag.getArtist());
			Artist albumArtist = artist;

			if (!StringUtils.isBlank(tag.getAlbumArtist()) && !tag.getArtist().equalsIgnoreCase(tag.getAlbumArtist())) {
				albumArtist = this.addArtist(tag.getAlbumArtist());
			}

			final Genre genre = this.addGenre(tag.getGenre());
			final Album album = this.addAlbum(albumArtist, tag.getAlbum(), tag.getYear(), tag.getCoverArt());
			final Track track = this.addTrack(artist, album, genre, tag.getTrack(), tag.getNumber(), tag.getLength(), tag
					.getCoverArt(), file, event.getLibrary());

			if (track != null) {
				log.info("Track added: {}", track);
			}

		} catch (Exception e) {
			log.error("Unable to handle unknown track event: {}", event, e);
		}
	}

	private String addCoverArt(BufferedImage image) {

		String coverArt = null;

		try {
			coverArt = this.coverArtService.cacheCoverArt(image);
		} catch (CoverArtException e) {
			log.error("Could not create cover image file, path: {}", coverArt, e);
		} catch (Exception e) {
			log.error("Error adding coverArt", e);
		}

		return coverArt;
	}

	private Genre addGenre(String name) {

		if (StringUtils.isBlank(name)) {
			return null;
		}

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

	private Track addTrack(Artist artist, Album album, Genre genre, String name, Integer number, Long length,
			BufferedImage image, File file, Library library) {

		// track name MUST have a value
		if (StringUtils.isBlank(name)) {
			log.error("Cannot add track: name was blank, file={}", file);
			return null;
		}

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
				track.setNumber(number);
				track.setLength(length);
				track.setFileModificationDate(new Date(file.lastModified())); // TODO
				track.setPath(file.getCanonicalPath());

				// Use albums covert art, otherwise use tracks
				if (album != null && album.getCoverArt() != null) {
					track.setCoverArt(album.getCoverArt());
				} else {
					if (image != null) {
						track.setCoverArt(this.addCoverArt(image));
					}
				}

				this.db.create(new TrackCreateQuery(track));
			}

			return track;

		} catch (Exception e) {
			log.error("Error adding track, name: {}", name, e);
		}

		return null;
	}

	private Album addAlbum(Artist artist, String name, String year, BufferedImage image) {

		if (StringUtils.isBlank(name)) {
			return null;
		}

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

	private Artist addArtist(String name) {

		if (StringUtils.isBlank(name)) {
			return null;
		}

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
