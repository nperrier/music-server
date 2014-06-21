package com.perrier.music.entity.track;

import java.util.Collections;
import java.util.List;

import org.hibernate.FetchMode;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.perrier.music.db.DBException;
import com.perrier.music.db.FindQuery;
import com.perrier.music.entity.album.Album;

public class TracksFindAllByAlbumIdQuery extends FindQuery<List<Track>> {

	private final long id;

	public TracksFindAllByAlbumIdQuery(long id) {
		this.id = id;
	}
	
	@Override
	public List<Track> query(Session session) throws DBException {

		Album album = (Album) session.createCriteria(Album.class)
				.setFetchMode("tracks", FetchMode.JOIN)
				.add(Restrictions.idEq(id))
				.uniqueResult();

		if (album == null) {
			// TODO Throw exception, return empty list or null?
			return Collections.emptyList();
		}

		List<Track> tracks = album.getTracks();

		return tracks;
	}
}
