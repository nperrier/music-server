package com.perrier.music.entity.album;

import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;

import com.google.inject.Inject;
import com.perrier.music.db.DBException;
import com.perrier.music.db.FindQuery;
import com.perrier.music.db.IDatabase;

public class AlbumProvider {

	private final IDatabase db;

	@Inject
	public AlbumProvider(IDatabase db) {
		this.db = db;
	}

	public Album findById(final long id) throws DBException {
		Album album = this.db.find(new AlbumFindByIdQuery(id));
		return album;
	}

	public Album findByIdWithTracks(final long id) throws DBException {
		try {
			this.db.openSession();

			Album album = this.db.find(new AlbumFindByIdQuery(id));
			if (album == null) {
				return null;
			}
			// Force Tracks to load, otherwise we'll get LazyInitializationException if the session is closed
			Hibernate.initialize(album.getTracks());

			return album;
		} finally {
			this.db.closeSession();
		}
	}

	public void create(Album album) throws DBException {
		this.db.create(new AlbumCreateQuery(album));
	}

	public List<Album> findAll() throws DBException {
		List<Album> albums = this.db.find(new AlbumFindAllQuery());
		return albums;
	}

	public List<Album> findAllByArtistId(Long id) throws DBException {
		List<Album> albums = this.db.find(new AlbumFindAllByArtistIdQuery(id));
		return albums;
	}

	/**
	 * Deletes an Album if there are no Tracks associated with it
	 *
	 * @param album
	 * @return true, if album was deleted, else false
	 * @throws DBException
	 */
	public boolean deleteIfOrphaned(Album album) throws DBException {
		try {
			this.db.beginTransaction();
			final long albumId = album.getId();
			boolean removed = false;

			FindQuery<List<Long>> trackIdFindByAlbumQuery = new FindQuery<List<Long>>() {

				@Override
				public List<Long> query(Session session) throws DBException {
					Query q = session.createQuery("" //
							+ "select distinct t.id\n" //
							+ "from Track t\n" //
							+ "join t.album a\n" //
							+ "where a.id = :albumId");
					q.setLong("albumId", albumId);
					return q.list();
				}
			};

			List<Long> trackIds = this.db.find(trackIdFindByAlbumQuery);

			if (trackIds.isEmpty()) {
				this.db.delete(new AlbumDeleteQuery(album));
				removed = true;
			}

			this.db.commit();

			return removed;

		} finally {
			this.db.endTransaction();
		}
	}

}
