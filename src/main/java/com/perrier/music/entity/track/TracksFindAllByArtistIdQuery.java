package com.perrier.music.entity.track;

import java.util.Collections;
import java.util.List;

import org.hibernate.FetchMode;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.perrier.music.db.DBException;
import com.perrier.music.db.FindQuery;
import com.perrier.music.entity.artist.Artist;

public class TracksFindAllByArtistIdQuery extends FindQuery<List<Track>> {

	private final long id;

	public TracksFindAllByArtistIdQuery(long id) {
		this.id = id;
	}

	@Override
	public List<Track> query(Session session) throws DBException {

		Artist artist = (Artist) session.createCriteria(Artist.class)
				.setFetchMode("tracks", FetchMode.JOIN)
				.add(Restrictions.idEq(id))
				.uniqueResult();

		if (artist == null) {
			// TODO Throw exception, return empty list or null?
			return Collections.emptyList();
		}

		List<Track> tracks = artist.getTracks();
		
		return tracks;
	}

}
