package com.perrier.music.entity.genre;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import com.google.inject.Inject;
import com.perrier.music.db.DBException;
import com.perrier.music.db.FindQuery;
import com.perrier.music.db.IDatabase;

public class GenreProvider {

	private final IDatabase db;

	@Inject
	public GenreProvider(IDatabase db) {
		this.db = db;
	}

	public List<Genre> findAll() throws DBException {
		List<Genre> genres = this.db.find(new GenreFindAllQuery());
		return genres;
	}

	/**
	 * Deletes a Genre if there are no Tracks associated with it
	 * 
	 * @param genre
	 * @return true, if genre was deleted, else false
	 * @throws DBException
	 */
	public boolean deleteIfOrphaned(Genre genre) throws DBException {
		try {
			this.db.beginTransaction();
			final long genreId = genre.getId();
			boolean removed = false;

			FindQuery<List<Long>> genreFindByIdQuery = new FindQuery<List<Long>>() {

				@Override
				public List<Long> query(Session session) throws DBException {
					Query q = session.createQuery("" //
							+ "select distinct t.id\n" //
							+ "from Track t\n" //
							+ "join t.genre g\n" //
							+ "where g.id = :genreId");
					q.setLong("genreId", genreId);
					return q.list();
				}
			};

			List<Long> trackIds = this.db.find(genreFindByIdQuery);

			if (trackIds.isEmpty()) {
				this.db.delete(new GenreDeleteQuery(genre));
				removed = true;
			}

			this.db.commit();

			return removed;
		} finally {
			this.db.endTransaction();
		}
	}
}
