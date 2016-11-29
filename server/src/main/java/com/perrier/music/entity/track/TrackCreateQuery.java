package com.perrier.music.entity.track;

import org.hibernate.Session;

import com.perrier.music.db.CreateQuery;
import com.perrier.music.db.DBException;

public class TrackCreateQuery extends CreateQuery<Track> {

	private final Track track;
	
	public TrackCreateQuery(Track track) {
		this.track = track;
	}
	
	@Override
	public Track query(Session session) throws DBException {
		session.save(this.track);
		return track;
	}

}
