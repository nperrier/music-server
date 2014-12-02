package com.perrier.music.entity.playlist;

import org.hibernate.Query;
import org.hibernate.Session;

import com.perrier.music.db.DBException;
import com.perrier.music.db.DeleteQuery;

public class PlaylistTrackDeleteQuery extends DeleteQuery {

	private final long trackId;

	public PlaylistTrackDeleteQuery(long trackId) {
		this.trackId = trackId;
	}

	@Override
	public void query(Session session) throws DBException {
		Query q = session.createQuery("delete from PlaylistTrack where trackId = :trackId");
		q.setLong("trackId", this.trackId);
		q.executeUpdate();
	}
}
