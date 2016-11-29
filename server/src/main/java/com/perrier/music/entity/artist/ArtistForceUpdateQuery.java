package com.perrier.music.entity.artist;

import org.hibernate.Session;

import com.perrier.music.db.DBException;
import com.perrier.music.db.ForceUpdateQuery;

public class ArtistForceUpdateQuery extends ForceUpdateQuery<Artist> {

	private final Artist artist;
	
	public ArtistForceUpdateQuery(Artist artist) {
		this.artist = artist;
	}
	
	@Override
	public Artist query(Session session) throws DBException {
        session.saveOrUpdate(artist);
        return artist;
	}
}
