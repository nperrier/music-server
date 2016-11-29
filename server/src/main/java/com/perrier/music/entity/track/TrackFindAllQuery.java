package com.perrier.music.entity.track;

import java.util.List;

import org.hibernate.Session;

import com.perrier.music.db.DBException;
import com.perrier.music.db.FindQuery;

public class TrackFindAllQuery extends FindQuery<List<Track>> {

	@Override
	public List<Track> query(Session session) throws DBException {
		@SuppressWarnings("unchecked")
		List<Track> tracks = session.createCriteria(Track.class).list();
		return tracks;
	}
}
