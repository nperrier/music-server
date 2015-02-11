package com.perrier.music.entity.track;

import org.hibernate.Query;
import org.hibernate.Session;

import com.perrier.music.db.DBException;
import com.perrier.music.db.FindQuery;

public class TrackFindByNameAndArtistIdAndAlbumIdQuery extends FindQuery<Track> {

	private final String name;
	private final long artistId;
	private final long albumId;

	public TrackFindByNameAndArtistIdAndAlbumIdQuery(String name, long artistId, long albumId) {
		this.name = name;
		this.artistId = artistId;
		this.albumId = albumId;
	}

	@Override
	public Track query(Session session) throws DBException {
		Query q = session.createQuery("" //
				+ "from Track " //
				+ "where name = lower(:name) " //
				+ "and artist.id = :artistId " //
				+ "and album.id = :albumId");
		q.setString("name", this.name);
		q.setLong("artistId", this.artistId);
		q.setLong("albumId", this.albumId);

		return (Track) q.uniqueResult();
	}
}
