package com.perrier.music.entity.album;

import java.util.List;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.Session;

import com.perrier.music.db.DBException;
import com.perrier.music.db.FindQuery;

public class AlbumFindByIdsQuery extends FindQuery<List<Album>> {

	private final Set<Long> ids;

	public AlbumFindByIdsQuery(Set<Long> ids) {
		this.ids = ids;
	}

	@Override
	public List<Album> query(Session session) throws DBException {
		Query q = session.createQuery("from Album where id in (:ids)");
		q.setParameterList("ids", this.ids);
		List<Album> albums = (List<Album>) q.list();
		return albums;
	}
}
