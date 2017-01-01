package com.perrier.music.search;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.perrier.music.db.DBException;
import com.perrier.music.db.FindQuery;
import com.perrier.music.entity.album.Album;
import com.perrier.music.entity.album.AlbumFindByIdsQuery;
import com.perrier.music.entity.artist.Artist;
import com.perrier.music.entity.artist.ArtistFindByIdsQuery;
import com.perrier.music.entity.track.Track;
import com.perrier.music.entity.track.TrackFindByIdsQuery;

class FullTextSearchByTableQuery extends FindQuery<SearchResults> {

	private static final Logger log = LoggerFactory.getLogger(FullTextSearchByTableQuery.class);

	private static final String FT_SQL = "select id from :table where search_name @@ plainto_tsquery(?)";

	private final String query;
	private final SearchTable table;

	FullTextSearchByTableQuery(String query, SearchTable table) {
		this.query = query;
		this.table = table;
	}

	@Override
	public SearchResults query(Session session) throws DBException {
		SearchResults searchResults = new SearchResults();

		final Set<Long> ids = new HashSet<>();
		session.doWork(connection -> {
			ids.addAll(getSearchIds(connection));
		});

		if (!ids.isEmpty()) {
			switch (this.table) {
			case ALBUM:
				List<Album> albums = db.find(new AlbumFindByIdsQuery(ids));
				searchResults.setAlbums(albums);
				break;
			case ARTIST:
				List<Artist> artists = db.find(new ArtistFindByIdsQuery(ids));
				searchResults.setArtists(artists);
				break;
			case TRACK:
				List<Track> tracks = db.find(new TrackFindByIdsQuery(ids));
				searchResults.setTracks(tracks);
				break;
			default:
				throw new RuntimeException("Unknown database table: " + table);
			}
		}

		return searchResults;
	}

	private Set<Long> getSearchIds(Connection connection) throws SQLException {
		Set<Long> ids = new HashSet<>();
		String searchSQL = FT_SQL.replace(":table", table.name());
		try (PreparedStatement st = connection.prepareStatement(searchSQL)) {
			st.setString(1, this.query);

			try (ResultSet rs = st.executeQuery()) {
				while (rs.next()) {
					long id = rs.getLong(1);
					ids.add(id);
				}
				log.debug("table: {}, ids: {}", table, ids);
			}

			return ids;
		}
	}
}
