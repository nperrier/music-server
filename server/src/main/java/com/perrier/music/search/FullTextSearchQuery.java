package com.perrier.music.search;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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

class FullTextSearchQuery extends FindQuery<SearchResults> {

	private static final Logger log = LoggerFactory.getLogger(FullTextSearchQuery.class);

	/**
	 * Query to get the total count of search results that match the search query for a particular table
	 */
	private static final String FT_COUNT_SQL = "select count(*) from :table where search_name @@ plainto_tsquery(?)";

	/**
	 * Query to get the ids that match the search query for a particular table
	 */
	private static final String FT_SQL = "select id from :table where search_name @@ plainto_tsquery(?) limit ?";
	/**
	 * The default search limit per table
	 */
	private static final int FT_SQL_LIMIT = 10;

	private final String query;

	FullTextSearchQuery(String query) {
		this.query = query;
	}

	@Override
	public SearchResults query(Session session) throws DBException {
		Map<SearchTable, Set<Long>> searchQueries = new HashMap<>();
		Map<SearchTable, Long> searchTotals = new HashMap<>();

		SearchResults searchResults = new SearchResults();

		session.doWork(connection -> {
			// ids
			Map<SearchTable, Set<Long>> queries = getSearchQueries(connection);
			searchQueries.putAll(queries);
			// totals
			Map<SearchTable, Long> totals = getSearchCountQueries(connection);
			searchTotals.putAll(totals);
		});

		searchQueries.entrySet().stream()
				.filter((entry) -> !entry.getValue().isEmpty()) // don't run query if no ids
				.forEach((entry) -> {
					SearchTable table = entry.getKey();
					Set<Long> ids = entry.getValue();
					switch (table) {
					case ALBUM:
						List<Album> albums = db.find(new AlbumFindByIdsQuery(ids));
						searchResults.setAlbums(albums);
						Long albumsTotal = searchTotals.get(table);
						searchResults.setAlbumsTotal(albumsTotal);
						break;
					case ARTIST:
						List<Artist> artists = db.find(new ArtistFindByIdsQuery(ids));
						searchResults.setArtists(artists);
						Long artistsTotal = searchTotals.get(table);
						searchResults.setArtistsTotal(artistsTotal);
						break;
					case TRACK:
						List<Track> tracks = db.find(new TrackFindByIdsQuery(ids));
						searchResults.setTracks(tracks);
						Long tracksTotal = searchTotals.get(table);
						searchResults.setTracksTotal(tracksTotal);
						break;
					default:
						throw new RuntimeException("Unknown database table: " + table);
					}
				});

		return searchResults;
	}

	private Map<SearchTable, Set<Long>> getSearchQueries(Connection connection) throws SQLException {
		Map<SearchTable, Set<Long>> tableIds = new HashMap<>();

		Set<Long> artistQueries = getSearchQueries(connection, SearchTable.ARTIST);
		Set<Long> albumQueries = getSearchQueries(connection, SearchTable.ALBUM);
		Set<Long> trackQueries = getSearchQueries(connection, SearchTable.TRACK);

		tableIds.put(SearchTable.ARTIST, artistQueries);
		tableIds.put(SearchTable.ALBUM, albumQueries);
		tableIds.put(SearchTable.TRACK, trackQueries);

		return tableIds;
	}

	private Set<Long> getSearchQueries(Connection connection, SearchTable table) throws SQLException {
		Set<Long> ids = new HashSet<>();
		String searchSQL = FT_SQL.replace(":table", table.name());
		try (PreparedStatement st = connection.prepareStatement(searchSQL)) {
			st.setString(1, this.query);
			st.setInt(2, FT_SQL_LIMIT);

			try (ResultSet rs = st.executeQuery()) {
				while (rs.next()) {
					Long id = rs.getLong(1);
					ids.add(id);
				}
				log.debug("table: {}, primaryKeys: {}", table, ids);
			}

			return ids;
		}
	}

	private Map<SearchTable, Long> getSearchCountQueries(Connection connection) throws SQLException {
		Map<SearchTable, Long> tableIds = new HashMap<>();

		Long artistCount = getSearchCountQueries(connection, SearchTable.ARTIST);
		Long albumCount = getSearchCountQueries(connection, SearchTable.ALBUM);
		Long trackCount = getSearchCountQueries(connection, SearchTable.TRACK);

		tableIds.put(SearchTable.ARTIST, artistCount);
		tableIds.put(SearchTable.ALBUM, albumCount);
		tableIds.put(SearchTable.TRACK, trackCount);

		return tableIds;
	}

	private Long getSearchCountQueries(Connection connection, SearchTable table) throws SQLException {
		Long count;
		String searchCountSQL = FT_COUNT_SQL.replace(":table", table.name());
		try (PreparedStatement st = connection.prepareStatement(searchCountSQL)) {
			st.setString(1, this.query);

			try (ResultSet rs = st.executeQuery()) {
				rs.next();
				count = rs.getLong(1);
				log.debug("table: {}, count: {}", table, count);
			}

			return count;
		}
	}
}
