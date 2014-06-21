package com.perrier.music.entity.artist;

import java.util.List;

import com.google.inject.Inject;
import com.perrier.music.db.DBException;
import com.perrier.music.db.IDatabase;

public class ArtistProvider {

	private IDatabase db;

	@Inject
	public ArtistProvider(IDatabase db) {
		this.db = db;
	}

	public Artist findById(final long id) throws DBException {
		Artist artist = this.db.find(new ArtistFindByIdQuery(id));
		return artist;
	}

	public void create(Artist artist) throws DBException {
		this.db.create(new ArtistCreateQuery(artist));
	}

	public List<Artist> findAll() throws DBException {
		List<Artist> artists = this.db.find(new ArtistFindAllQuery());
		return artists;
	}
}
