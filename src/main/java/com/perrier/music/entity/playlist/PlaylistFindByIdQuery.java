package com.perrier.music.entity.playlist;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.perrier.music.db.DBException;
import com.perrier.music.db.FindQuery;

public class PlaylistFindByIdQuery extends FindQuery<Playlist> {

	private final long id;
	private final boolean includeTracks;

	public PlaylistFindByIdQuery(long id, boolean includeTracks) {
		this.id = id;
		this.includeTracks = includeTracks;
	}

	@Override
	public Playlist query(Session session) throws DBException {

		Criteria c = session.createCriteria(Playlist.class);
		c.add(Restrictions.idEq(this.id));

		if (this.includeTracks) {
			c.setFetchMode("playlistTracks", FetchMode.JOIN);
		}

		Playlist playlist = (Playlist) c.uniqueResult();
		return playlist;
	}

}
