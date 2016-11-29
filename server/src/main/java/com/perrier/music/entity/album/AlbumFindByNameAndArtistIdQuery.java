package com.perrier.music.entity.album;

import static org.hibernate.criterion.Restrictions.eq;
import static org.hibernate.criterion.Restrictions.isNull;

import org.hibernate.Criteria;
import org.hibernate.Session;

import com.perrier.music.db.DBException;
import com.perrier.music.db.FindQuery;

public class AlbumFindByNameAndArtistIdQuery extends FindQuery<Album> {

	private final String name;
	private final Long artistId;

	/**
	 * Search for an Album by name that does not have an Artist
	 * 
	 * @param name
	 */
	public AlbumFindByNameAndArtistIdQuery(String name) {
		this(name, null);
	}

	/**
	 * Search for an Album by name and Artist
	 * 
	 * @param name
	 * @param artistId
	 */
	public AlbumFindByNameAndArtistIdQuery(String name, Long artistId) {
		this.name = name;
		this.artistId = artistId;
	}

	@Override
	public Album query(Session session) throws DBException {
		// Query q = session.createQuery("from Album where lower(name) = lower(:name) and artist.id = :artistId");
		Criteria c = session.createCriteria(Album.class);
		c.add(eq("name", name).ignoreCase());
		if (this.artistId == null) {
			c.add(isNull("artist.id"));
		} else {
			c.add(eq("artist.id", artistId));
		}
		// return (Album) q.uniqueResult();
		return (Album) c.uniqueResult();
	}

}
