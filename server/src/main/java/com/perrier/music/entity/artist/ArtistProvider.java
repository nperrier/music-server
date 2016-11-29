package com.perrier.music.entity.artist;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import com.google.inject.Inject;
import com.perrier.music.db.DBException;
import com.perrier.music.db.FindQuery;
import com.perrier.music.db.IDatabase;

public class ArtistProvider {

	private IDatabase db;

	@Inject
	public ArtistProvider(IDatabase db) {
		this.db = db;
	}

	public Artist findById(final long id) throws DBException {
		Artist artist = this.db.find(new ArtistFindByIdQuery(id));
		return artist;
	}

	public void create(Artist artist) throws DBException {
		this.db.create(new ArtistCreateQuery(artist));
	}

	public List<Artist> findAll() throws DBException {
		List<Artist> artists = this.db.find(new ArtistFindAllQuery());
		return artists;
	}

	/**
	 * Deletes an Artist if there are no Tracks or Albums associated with it
	 *
	 * @param artist
	 * @return true, if artist was deleted, else false
	 * @throws DBException
	 */
	public boolean deleteIfOrphaned(Artist artist) throws DBException {
		try {
			this.db.beginTransaction();
			final long artistId = artist.getId();
			boolean removed = false;

			// get all track ids associated with artist
			List<Long> trackIds = this.db.find(new FindQuery<List<Long>>() {

				@Override
				public List<Long> query(Session session) throws DBException {
					Query q = session.createQuery("" //
							+ "select distinct t.id\n" //
							+ "from Track t\n" //
							+ "join t.artist a\n" //
							+ "where a.id = :artistId");
					q.setLong("artistId", artistId);
					return q.list();
				}
			});

			// get all album ids associated with artist
			List<Long> albumIds = this.db.find(new FindQuery<List<Long>>() {

				@Override
				public List<Long> query(Session session) throws DBException {
					Query q = session.createQuery("" //
							+ "select distinct al.id\n" //
							+ "from Album al\n" //
							+ "join al.artist a\n" //
							+ "where a.id = :artistId");
					q.setLong("artistId", artistId);
					return q.list();
				}
			});

			if (trackIds.isEmpty() && albumIds.isEmpty()) {
				this.db.delete(new ArtistDeleteQuery(artist));
				removed = true;
			}

			this.db.commit();

			return removed;

		} finally {
			this.db.endTransaction();
		}
	}
}
