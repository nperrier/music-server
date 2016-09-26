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
	private static final String FT_COUNT_SQL = "select count(*) from ft_search_data(?, 0, 0) where table = ?";
	/**
	 * Query to get the ids that match the search query for a particular table
	 */
	private static final String FT_SQL = "select keys from ft_search_data(?, 0, 0) where table = ? limit ?";
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

		searchQueries.forEach((table, ids) -> {
			switch (table) {
			case ALBUM:
				List<Album> albums = db.find(new AlbumFindByIdsQuery(ids));
				searchResults.setAlbums(albums);
				Long albumsTotal = searchTotals.get(table);
				searchResults.setAlbumsTotal(albumsTotal);
				break;
			case ARTIST:
				List<Artist> artists = db.find(new ArtistFindByIdsQuery(ids));
				Long artistsTotal = searchTotals.get(table);
				searchResults.setArtistsTotal(artistsTotal);
				searchResults.setArtists(artists);
				break;
			case TRACK:
				List<Track> tracks = db.find(new TrackFindByIdsQuery(ids));
				Long tracksTotal = searchTotals.get(table);
				searchResults.setTracksTotal(tracksTotal);
				searchResults.setTracks(tracks);
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

		try (PreparedStatement st = connection.prepareStatement(FT_SQL)) {
			st.setString(1, this.query);
			st.setString(2, table.name());
			st.setInt(3, FT_SQL_LIMIT);

			try (ResultSet rs = st.executeQuery()) {
				while (rs.next()) {
					Object[] primaryKeys = (Object[]) rs.getArray(1).getArray();
					for (Object o : primaryKeys) {
						ids.add(Long.parseLong((String) o));
					}
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
		try (PreparedStatement st = connection.prepareStatement(FT_COUNT_SQL)) {
			st.setString(1, this.query);
			st.setString(2, table.name());

			try (ResultSet rs = st.executeQuery()) {
				rs.next();
				count = rs.getLong(1);
				log.debug("table: {}, count: {}", table, count);
			}

			return count;
		}
	}
}
