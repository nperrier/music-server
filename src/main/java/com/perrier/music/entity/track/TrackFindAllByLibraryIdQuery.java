package com.perrier.music.entity.track;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import com.perrier.music.db.DBException;
import com.perrier.music.db.FindQuery;

public class TrackFindAllByLibraryIdQuery extends FindQuery<List<Track>> {

	private final long id;

	public TrackFindAllByLibraryIdQuery(long id) {
		this.id = id;
	}

	@Override
	public List<Track> query(Session session) throws DBException {

		Query q = session.createQuery("select t from Track t where t.library.id = :id");
		q.setLong("id", this.id);

		return q.list();
	}

}
