package com.perrier.music.entity.track;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

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

public class TrackAlbumUpdater extends AbstractTrackUpdater<Album> {

	private static final Logger log = LoggerFactory.getLogger(TrackArtistUpdater.class);

	private final UpdateResult<Artist> artistUpdate;

	@AssistedInject
	public TrackAlbumUpdater(@Assisted Track track, @Assisted UpdateResult<Artist> artistUpdate) {
		super(track);
		this.artistUpdate = artistUpdate;
	}

	public UpdateResult<Album> handleUpdate(String albumName) throws DBException {

		// normalize input
		albumName = trimToEmpty(albumName);
		albumName = albumName.replaceAll("\\s+", " "); // collapse an extra spaces

		Album trackAlbum = track.getAlbum();

		boolean create = false;
		boolean update = false;
		boolean remove = false;

		if (trackAlbum == null) {
			if (!isBlank(albumName)) {
				log.debug("Added new album to track, albumName={}, trackAlbum={}", albumName, track);
				create = true;
			}
		} else if (isBlank(albumName)) {
			log.debug("Removing album from track, albumName={}, track={}", trackAlbum.getName(), track);
			remove = true;
		} else if (!trackAlbum.getName().equals(albumName)) {
			// Check if changes are superficial (e.g., capitalization)
			if (trackAlbum.getName().equalsIgnoreCase(albumName) && !artistUpdate.getChanged()) {
				log.debug("Updating album for track, newAlbumName={}, track={}", albumName, track);
				update = true;
			}
			// But if the artist changed or the changes were not superficial, we will have to create a new album
			else {
				log.debug("Changing album for track, newAlbumName={}, track={}", albumName, track);
				create = true;
			}
		} else if (artistUpdate.getChanged()) {
			// the album wasn't changed, but the artist might have, so we have to change the album
			// to associate it with the new artist value
			log.debug("Album didn't change, but the artist did.  Changing the album artist, artist={}, track={}",
					artistUpdate, track);
			create = true;
		}

		Album albumUpdate = trackAlbum;

		if (create) {
			Artist artist = track.getArtist();

			if (artistUpdate.getChanged()) {
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
		} else if (update) {
			albumUpdate.setName(albumName);
			albumUpdate = this.db.update(new AlbumUpdateQuery(albumUpdate));
		} else if (remove) {
			albumUpdate = null;
		}

		// not including 'update' because 'changed' here means the 'association to track' changed
		final boolean changed = (create || remove);

		return new UpdateResult<Album>(albumUpdate, changed);
	}
}
