package com.perrier.music.entity.track;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import com.perrier.music.db.DBException;
import com.perrier.music.db.FindQuery;

/**
 * Finds random list of Tracks. Used for 'shuffle' feature
 */
public class TrackFindRandomQuery extends FindQuery<List<Track>> {

	private static final int LIMIT = 100;

	public TrackFindRandomQuery() {
	}

	@Override
	public List<Track> query(Session session) throws DBException {
		Query q = session.createQuery("from Track t order by rand()");
		q.setMaxResults(LIMIT);

		@SuppressWarnings("unchecked")
		List<Track> list = q.list();

		return list;
	}
}
