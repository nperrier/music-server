package com.perrier.music.entity.album;

import org.hibernate.Session;

import com.perrier.music.db.DBException;
import com.perrier.music.db.ForceUpdateQuery;

public class AlbumForceUpdateQuery extends ForceUpdateQuery<Album> {

	private final Album album;
	
	public AlbumForceUpdateQuery(Album album) {
		this.album = album;
	}
	
	@Override
	public Album query(Session session) throws DBException {
        session.saveOrUpdate(album);
        return album;
	}
}
