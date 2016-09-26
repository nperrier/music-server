package com.perrier.music.entity.artist;

import java.util.List;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.Session;

import com.perrier.music.db.DBException;
import com.perrier.music.db.FindQuery;

public class ArtistFindByIdsQuery extends FindQuery<List<Artist>> {

	private final Set<Long> ids;

	public ArtistFindByIdsQuery(Set<Long> ids) {
		this.ids = ids;
	}

	@Override
	public List<Artist> query(Session session) throws DBException {
		Query q = session.createQuery("from Artist where id in (:ids)");
		q.setParameterList("ids", this.ids);
		List<Artist> artists = (List<Artist>) q.list();
		return artists;
	}
}
