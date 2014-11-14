package com.perrier.music.entity.track;

import org.hibernate.Session;

import com.perrier.music.db.DBException;
import com.perrier.music.db.UpdateQuery;

public class TrackUpdateQuery extends UpdateQuery<Track> {

	private final Track track;

	public TrackUpdateQuery (Track track) {
		this.track = track;
	}

	@Override
	public Track query (Session session) throws DBException {
		session.update(track);
		return track;
	}
}
