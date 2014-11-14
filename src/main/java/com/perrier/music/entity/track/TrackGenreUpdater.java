package com.perrier.music.entity.track;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.perrier.music.db.DBException;
import com.perrier.music.entity.genre.Genre;
import com.perrier.music.entity.genre.GenreCreateQuery;
import com.perrier.music.entity.genre.GenreFindByNameQuery;
import com.perrier.music.entity.genre.GenreUpdateQuery;

public class TrackGenreUpdater extends AbstractTrackUpdater<Genre> {

	private static final Logger log = LoggerFactory.getLogger(TrackGenreUpdater.class);

	@AssistedInject
	public TrackGenreUpdater(@Assisted Track track) {
		super(track);
	}

	public UpdateResult<Genre> handleUpdate(String genreName) throws DBException {

		// normalize input
		genreName = trimToEmpty(genreName);
		genreName = genreName.replaceAll("\\s+", " "); // collapse an extra spaces

		Genre trackGenre = track.getGenre();

		boolean create = false;
		boolean update = false;
		boolean remove = false;

		if (trackGenre == null) {
			if (!isBlank(genreName)) {
				log.debug("Added new genre to track, genreName={}, track={}", genreName, track);
				create = true;
			}
		} else if (isBlank(genreName)) {
			log.debug("Removing genre from track, genreName={}, track={}", trackGenre.getName(), track);
			remove = true;
		} else if (!trackGenre.getName().equals(genreName)) {
			// Checks if changes are superficial (e.g., capitalization)
			if (trackGenre.getName().equalsIgnoreCase(genreName)) {
				log.debug("Updating genre for track, newGenreName={}, track={}", genreName, track);
				update = true;
			} else {
				log.debug("Changing genre for track, newGenreName={}, track={}", genreName, track);
				create = true;
			}
		}

		Genre genreUpdate = trackGenre;

		if (create) {
			genreUpdate = this.db.find(new GenreFindByNameQuery(genreName));
			if (genreUpdate == null) {
				genreUpdate = new Genre();
				genreUpdate.setName(genreName);
				genreUpdate = this.db.create(new GenreCreateQuery(genreUpdate));
			}
		} else if (update) {
			genreUpdate.setName(genreName);
			genreUpdate = this.db.update(new GenreUpdateQuery(genreUpdate));
		} else if (remove) {
			genreUpdate = null;
		}

		// not including 'update' because 'changed' here means the 'association to track' changed
		final boolean changed = (create || remove);

		return new UpdateResult<Genre>(genreUpdate, changed);
	}
}
