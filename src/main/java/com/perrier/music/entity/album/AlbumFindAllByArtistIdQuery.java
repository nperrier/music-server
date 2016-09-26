package com.perrier.music.entity.album;

import java.util.Collections;
import java.util.List;

import org.hibernate.FetchMode;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.perrier.music.db.DBException;
import com.perrier.music.db.FindQuery;
import com.perrier.music.entity.artist.Artist;

public class AlbumFindAllByArtistIdQuery extends FindQuery<List<Album>> {

	private final long id;

	public AlbumFindAllByArtistIdQuery(long id) {
		this.id = id;
	}

	@Override
	public List<Album> query(Session session) throws DBException {

		Artist artist = (Artist) session.createCriteria(Artist.class) //
				.setFetchMode("albums", FetchMode.JOIN) //
				.add(Restrictions.idEq(id)) //
				.uniqueResult();

		if (artist == null) {
			// TODO Throw exception, return empty list or null?
			return Collections.emptyList();
		}

		List<Album> albums = artist.getAlbums();

		return albums;
	}

}
