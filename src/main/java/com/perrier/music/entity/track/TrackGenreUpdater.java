package com.perrier.music.entity.track;

import static org.apache.commons.lang3.StringUtils.isBlank;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.perrier.music.db.DBException;
import com.perrier.music.entity.genre.Genre;
import com.perrier.music.entity.genre.GenreCreateQuery;
import com.perrier.music.entity.genre.GenreFindByNameQuery;
import com.perrier.music.entity.genre.GenreUpdateQuery;
import com.perrier.music.entity.track.AbstractTrackUpdater.UpdateResult.Change;

public class TrackGenreUpdater extends AbstractTrackUpdater<Genre> {

	private static final Logger log = LoggerFactory.getLogger(TrackGenreUpdater.class);

	@AssistedInject
	public TrackGenreUpdater(@Assisted Track track) {
		super(track);
	}

	public UpdateResult<Genre> handleUpdate(String trackGenreName) throws DBException {
		String genreName = normalizeInput(trackGenreName);
		Genre trackGenre = track.getGenre();
		Change change = determineChange(genreName, trackGenre);
		UpdateResult updateResult = doChange(genreName, trackGenre, change);

		return updateResult;
	}

	private Change determineChange(String genreName, Genre trackGenre) {
		Change change = Change.NONE;

		if (trackGenre == null) {
			if (!isBlank(genreName)) {
				log.debug("Added new genre to track, genreName={}, track={}", genreName, track);
				change = Change.CREATED;
			}
		} else if (isBlank(genreName)) {
			log.debug("Removing genre from track, genreName={}, track={}", trackGenre.getName(), track);
			change = Change.DELETED;
		} else if (!trackGenre.getName().equals(genreName)) {
			// Checks if changes are superficial (e.g., capitalization)
			if (trackGenre.getName().equalsIgnoreCase(genreName)) {
				log.debug("Updating genre for track, newGenreName={}, track={}", genreName, track);
				change = Change.UPDATED;
			} else {
				log.debug("Changing genre for track, newGenreName={}, track={}", genreName, track);
				change = Change.CREATED;
			}
		}

		return change;
	}

	private UpdateResult doChange(String genreName, Genre trackGenre, Change change) throws DBException {
		Genre originalGenre = Genre.copy(trackGenre);
		Genre genreUpdate = trackGenre;

		switch (change) {
		case CREATED:
			genreUpdate = this.db.find(new GenreFindByNameQuery(genreName));
			if (genreUpdate == null) {
				genreUpdate = new Genre();
				genreUpdate.setName(genreName);
				genreUpdate = this.db.create(new GenreCreateQuery(genreUpdate));
			}
			break;
		case UPDATED:
			genreUpdate.setName(genreName);
			genreUpdate = this.db.update(new GenreUpdateQuery(genreUpdate));
			break;
		case DELETED:
			genreUpdate = null;
			break;
		case NONE:
			// noop
			break;
		default:
			throw new RuntimeException("Unknown change type: " + change);
		}

		return new UpdateResult<Genre>(originalGenre, genreUpdate, change);
	}
}
