package com.perrier.music.entity.track;

import org.hibernate.Session;

import com.perrier.music.db.DBException;
import com.perrier.music.db.DeleteQuery;

public class TrackDeleteQuery extends DeleteQuery {

	private final Track track;

	public TrackDeleteQuery(Track track) {
		this.track = track;
	}

	@Override
	public void query(Session session) throws DBException {

		session.delete(track);
	}

}
