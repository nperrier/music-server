package com.perrier.music.entity.track;

import static org.apache.commons.lang3.StringUtils.isBlank;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.perrier.music.db.DBException;
import com.perrier.music.entity.artist.Artist;
import com.perrier.music.entity.artist.ArtistCreateQuery;
import com.perrier.music.entity.artist.ArtistFindByNameQuery;
import com.perrier.music.entity.artist.ArtistUpdateQuery;
import com.perrier.music.entity.track.AbstractTrackUpdater.UpdateResult.Change;

public class TrackArtistUpdater extends AbstractTrackUpdater<Artist> {

	private static final Logger log = LoggerFactory.getLogger(TrackArtistUpdater.class);

	@AssistedInject
	public TrackArtistUpdater(@Assisted Track track) {
		super(track);
	}

	public UpdateResult<Artist> handleUpdate(String trackArtistName) throws DBException {

		String artistName = normalizeInput(trackArtistName);
		Artist trackArtist = track.getArtist();
		Change change = determineChange(artistName, trackArtist);
		UpdateResult updateResult = doChange(artistName, trackArtist, change);

		return updateResult;
	}

	private Change determineChange(String artistName, Artist trackArtist) {
		Change change = Change.NONE;

		if (trackArtist == null) {
			if (!isBlank(artistName)) {
				log.debug("Added new artist to track, artistName={}, trackArtist={}", artistName, track);
				change = Change.CREATED;
			}
		} else if (isBlank(artistName)) {
			log.debug("Removing artist from track, artistName={}, track={}", trackArtist.getName(), track);
			change = Change.DELETED;
		} else if (!trackArtist.getName().equals(artistName)) {
			// Checks if changes are superficial (e.g., capitalization)
			if (trackArtist.getName().equalsIgnoreCase(artistName)) {
				log.debug("Updating artist for track, newArtistName={}, track={}", artistName, track);
				change = Change.UPDATED;
			} else {
				log.debug("Changing artist for track, newArtistName={}, track={}", artistName, track);
				change = Change.CREATED;
			}
		}

		return change;
	}

	private UpdateResult doChange(String artistName, Artist trackArtist, Change change) throws DBException {
		Artist originalArtist = Artist.copy(trackArtist);
		Artist artistUpdate = trackArtist;

		switch (change) {
		case CREATED:
			artistUpdate = this.db.find(new ArtistFindByNameQuery(artistName));
			if (artistUpdate == null) {
				artistUpdate = new Artist();
				artistUpdate.setName(artistName);
				artistUpdate = this.db.create(new ArtistCreateQuery(artistUpdate));
			}
			break;
		case UPDATED:
			artistUpdate.setName(artistName);
			artistUpdate = this.db.update(new ArtistUpdateQuery(artistUpdate));
			break;
		case DELETED:
			artistUpdate = null;
			break;
		case NONE:
			// noop
			break;
		default:
			throw new RuntimeException("Unknown change type: " + change);
		}

		return new UpdateResult<Artist>(originalArtist, artistUpdate, change);
	}
}
