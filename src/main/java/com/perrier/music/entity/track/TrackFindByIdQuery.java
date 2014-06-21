package com.perrier.music.entity.track;

import org.hibernate.Session;

import com.perrier.music.db.DBException;
import com.perrier.music.db.FindQuery;

public class TrackFindByIdQuery extends FindQuery<Track> {

	private final long id;
	
	public TrackFindByIdQuery(long id) {
		this.id = id;
	}
	
	@Override
	public Track query(Session session) throws DBException {
		
		Track track = (Track) session.get(Track.class, this.id);
		return track;
	}

}
