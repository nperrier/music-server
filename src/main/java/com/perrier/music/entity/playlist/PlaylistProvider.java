package com.perrier.music.entity.playlist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.google.inject.Inject;
import com.perrier.music.db.DBException;
import com.perrier.music.db.IDatabase;
import com.perrier.music.dto.playlist.PlaylistDto;
import com.perrier.music.dto.playlist.PlaylistTrackDto;
import com.perrier.music.entity.album.Album;
import com.perrier.music.entity.album.AlbumFindByIdQuery;
import com.perrier.music.entity.track.Track;
import com.perrier.music.entity.track.TrackFindByIds;

public class PlaylistProvider {

	private final IDatabase db;

	@Inject
	public PlaylistProvider(IDatabase db) {
		this.db = db;
	}

	public List<Playlist> findAll(boolean includeTracks) throws DBException {
		List<Playlist> playlists = this.db.find(new PlaylistFindAllQuery(includeTracks));
		return playlists;
	}

	public Playlist findById(long id, boolean includeTracks) throws DBException {
		Playlist playlist = this.db.find(new PlaylistFindByIdQuery(id, includeTracks));
		return playlist;
	}

	public void create(Playlist playlist) throws DBException {
		this.db.create(new PlaylistCreateQuery(playlist));
	}

	public void delete(Playlist playlist) throws DBException {
		this.db.delete(new PlaylistDeleteQuery(playlist));
	}

	public void addAlbumToPlaylist(Playlist playlist, Long albumId, Integer position) throws DBException {
		try {
			this.db.beginTransaction();

			Album album = this.db.find(new AlbumFindByIdQuery(albumId));
			final List<Track> albumTracks = album.getTracks();
			final List<PlaylistTrack> playlistTracks = appendTracksToPlaylist(playlist, albumTracks, position);
			playlist.setPlaylistTracks(playlistTracks);
			this.db.update(new PlaylistUpdateQuery(playlist));

			this.db.commit();

		} finally {
			this.db.endTransaction();
		}
	}

	public void addTracksToPlaylist(Playlist playlist, List<Long> trackIds, Integer position) throws DBException {
		try {
			this.db.beginTransaction();

			List<Track> tracks = this.db.find(new TrackFindByIds(trackIds));
			// Sort tracks by the order specified by the trackIds list:
			Collections.sort(tracks, (left, right) -> {
				int lIndex = trackIds.indexOf(left.getId());
				int rIndex = trackIds.indexOf(right.getId());
				return (lIndex == rIndex ? 0 : (lIndex > rIndex ? 1 : -1));
			});

			final List<PlaylistTrack> playlistTracks = appendTracksToPlaylist(playlist, tracks, position);
			playlist.setPlaylistTracks(playlistTracks);
			this.db.update(new PlaylistUpdateQuery(playlist));

			this.db.commit();

		} finally {
			this.db.endTransaction();
		}
	}

	private List<PlaylistTrack> appendTracksToPlaylist(Playlist playlist, List<Track> tracks, Integer position)
			throws DBException {
		if (tracks.isEmpty()) {
			return Collections.emptyList();
		}

		List<PlaylistTrack> playlistTracks = playlist.getPlaylistTracks();

		// if position is not specified, append tracks to the end of the list
		int pos = (position != null ? position : playlistTracks.size());

		if (pos > playlistTracks.size() || pos < 0) {
			throw new DBException("Invalid position: " + pos);
		}

		for (final Track track : tracks) {
			PlaylistTrack playlistTrack = new PlaylistTrack();
			playlistTrack.setPlaylist(playlist);
			playlistTrack.setTrack(track);
			playlistTracks.add(pos, playlistTrack);
			pos++;
		}

		return playlistTracks;
	}

	public void updatePlaylistTracks(Playlist playlist, List<PlaylistTrackDto> trackDtos) throws DBException {
		List<PlaylistTrack> tracks = new ArrayList<>(trackDtos.size());
		trackDtos.forEach((t) -> {
			PlaylistTrack pt = new PlaylistTrack();
			pt.setId(t.getId());
			pt.setPlaylist(playlist);
			pt.setPosition(t.getPosition());
			pt.setTrack(pt.getTrack());
			tracks.add(pt.getPosition(), pt);
		});

		playlist.setPlaylistTracks(tracks);
		this.db.update(new PlaylistUpdateQuery(playlist));
	}

	public void removeTrack(Playlist playlist, long playlistTrackId) throws DBException {
		List<PlaylistTrack> playlistTracks = playlist.getPlaylistTracks();
		// find the playlistTrackId to remove
		for (Iterator<PlaylistTrack> i = playlistTracks.iterator(); i.hasNext(); ) {
			PlaylistTrack pt = i.next();
			if (playlistTrackId == pt.getId()) {
				i.remove();
				break;
			}
		}

		// hibernate will update position column:
		this.db.update(new PlaylistUpdateQuery(playlist));
	}

	public Playlist update(Playlist playlist, PlaylistDto playlistUpdateDto) {
		playlist.setName(playlistUpdateDto.getName());
		this.db.update(new PlaylistUpdateQuery(playlist));

		return playlist;
	}
}
