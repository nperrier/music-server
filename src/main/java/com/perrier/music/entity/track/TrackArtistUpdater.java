package com.perrier.music.entity.track;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.perrier.music.db.DBException;
import com.perrier.music.entity.artist.Artist;
import com.perrier.music.entity.artist.ArtistCreateQuery;
import com.perrier.music.entity.artist.ArtistFindByNameQuery;
import com.perrier.music.entity.artist.ArtistUpdateQuery;

public class TrackArtistUpdater extends AbstractTrackUpdater<Artist> {

	private static final Logger log = LoggerFactory.getLogger(TrackArtistUpdater.class);

	@AssistedInject
	public TrackArtistUpdater(@Assisted Track track) {
		super(track);
	}

	public UpdateResult<Artist> handleUpdate(String artistName) throws DBException {

		// normalize input
		artistName = trimToEmpty(artistName);
		artistName = artistName.replaceAll("\\s+", " "); // collapse an extra spaces

		Artist trackArtist = track.getArtist();

		boolean create = false;
		boolean update = false;
		boolean remove = false;

		if (trackArtist == null) {
			if (!isBlank(artistName)) {
				log.debug("Added new artist to track, artistName={}, trackArtist={}", artistName, track);
				create = true;
			}
		} else if (isBlank(artistName)) {
			log.debug("Removing artist from track, artistName={}, track={}", trackArtist.getName(), track);
			remove = true;
		} else if (!trackArtist.getName().equals(artistName)) {
			// Checks if changes are superficial (e.g., capitalization)
			if (trackArtist.getName().equalsIgnoreCase(artistName)) {
				log.debug("Updating artist for track, newArtistName={}, track={}", artistName, track);
				update = true;
			} else {
				log.debug("Changing artist for track, newArtistName={}, track={}", artistName, track);
				create = true;
			}
		}

		Artist artistUpdate = trackArtist;

		if (create) {
			artistUpdate = this.db.find(new ArtistFindByNameQuery(artistName));
			if (artistUpdate == null) {
				artistUpdate = new Artist();
				artistUpdate.setName(artistName);
				artistUpdate = this.db.create(new ArtistCreateQuery(artistUpdate));
			}
		} else if (update) {
			artistUpdate.setName(artistName);
			artistUpdate = this.db.update(new ArtistUpdateQuery(artistUpdate));
		} else if (remove) {
			artistUpdate = null;
		}

		// not including 'update' because 'changed' here means the 'association to track' changed
		final boolean changed = (create || remove);

		return new UpdateResult<Artist>(artistUpdate, changed);
	}
}
