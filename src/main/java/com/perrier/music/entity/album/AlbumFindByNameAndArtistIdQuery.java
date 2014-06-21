package com.perrier.music.entity.album;

import org.hibernate.Query;
import org.hibernate.Session;

import com.perrier.music.db.DBException;
import com.perrier.music.db.FindQuery;

public class AlbumFindByNameAndArtistIdQuery extends FindQuery<Album> {

	private final String name;
	private final long artistId;

	public AlbumFindByNameAndArtistIdQuery(String name, long artistId) {
		this.name = name;
		this.artistId = artistId;
	}

	@Override
	public Album query(Session session) throws DBException {

		Query q = session.createQuery("from Album where name = :name and artist.id = :artistId");
		q.setString("name", this.name);
		q.setLong("artistId", this.artistId);

		return (Album) q.uniqueResult();
	}

}
