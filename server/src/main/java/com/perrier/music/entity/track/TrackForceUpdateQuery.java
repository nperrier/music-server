package com.perrier.music.entity.track;

import org.hibernate.Session;

import com.perrier.music.db.DBException;
import com.perrier.music.db.ForceUpdateQuery;

public class TrackForceUpdateQuery extends ForceUpdateQuery<Track> {

	private final Track track;
	
	public TrackForceUpdateQuery(Track track) {
		this.track = track;
	}
	
	@Override
	public Track query(Session session) throws DBException {
		session.saveOrUpdate(track);
		return track;
	}
}
