package com.perrier.music.entity.track;

import java.util.List;

import org.hibernate.Session;

import com.perrier.music.db.DBException;
import com.perrier.music.db.FindQuery;

public class TrackSearchQuery extends FindQuery<List<Track>> {

	public TrackSearchQuery(String query) {
		super();
	}

	@Override
	public List<Track> query(Session session) throws DBException {
		return null;
	}
}
