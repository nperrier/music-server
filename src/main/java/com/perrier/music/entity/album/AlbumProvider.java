package com.perrier.music.entity.album;

import java.util.List;

import com.google.inject.Inject;
import com.perrier.music.db.DBException;
import com.perrier.music.db.IDatabase;

public class AlbumProvider {

	private IDatabase db;

	@Inject
	public AlbumProvider(IDatabase db) {
		this.db = db;
	}

	public Album findById(final long id) throws DBException {
		Album album = this.db.find(new AlbumFindByIdQuery(id));
		return album;
	}

	public void create(Album album) throws DBException {
		this.db.create(new AlbumCreateQuery(album));
	}

	public List<Album> findAll() throws DBException {
		List<Album> albums = this.db.find(new AlbumFindAllQuery());
		return albums;
	}

	public List<Album> findAllByArtistId(Long id) throws DBException {
		List<Album> albums = this.db.find(new AlbumsFindAllByArtistIdQuery(id));
		return albums;
	}

}
