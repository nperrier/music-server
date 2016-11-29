package com.perrier.music.entity.track;

import java.util.Collections;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import com.perrier.music.db.DBException;
import com.perrier.music.db.FindQuery;

public class TrackFindByIds extends FindQuery<List<Track>> {

	private final List<Long> trackIds;

	public TrackFindByIds(List<Long> trackIds) {
		this.trackIds = trackIds;
	}

	@Override
	public List<Track> query(Session session) throws DBException {

		if (this.trackIds.isEmpty()) {
			return Collections.emptyList();
		}

		Query q = session.createQuery("from Track t where t.id in (:ids)");
		q.setParameterList("ids", this.trackIds);
		@SuppressWarnings("unchecked")
		List<Track> tracks = q.list();
		return tracks;
	}

}
