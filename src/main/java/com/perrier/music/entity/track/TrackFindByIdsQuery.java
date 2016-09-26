package com.perrier.music.entity.track;

import java.util.List;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.Session;

import com.perrier.music.db.DBException;
import com.perrier.music.db.FindQuery;

public class TrackFindByIdsQuery extends FindQuery<List<Track>> {

	private final Set<Long> ids;

	public TrackFindByIdsQuery(Set<Long> ids) {
		this.ids = ids;
	}

	@Override
	public List<Track> query(Session session) throws DBException {
		Query q = session.createQuery("from Track where id in (:ids)");
		q.setParameterList("ids", this.ids);
		List<Track> tracks = (List<Track>) q.list();
		return tracks;
	}
}
