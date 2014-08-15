package com.perrier.music.entity.playlist;

import java.util.Collections;
import java.util.List;

import org.hibernate.Session;

import com.perrier.music.db.CreateQuery;
import com.perrier.music.db.DBException;
import com.perrier.music.entity.track.Track;

public class PlaylistAddTrackQuery extends CreateQuery<List<PlaylistTrack>> {

	private final Playlist playlist;
	private final List<Track> tracks;
	private final Integer position;

	public PlaylistAddTrackQuery(Playlist playlist, List<Track> tracks, Integer position) {
		this.playlist = playlist;
		this.tracks = tracks;
		this.position = position;
	}

	@Override
	public List<PlaylistTrack> query(Session session) throws DBException {

		if (this.tracks.isEmpty()) {
			return Collections.emptyList();
		}

		List<PlaylistTrack> playlistTracks = Collections.emptyList();

		try {
			this.db.beginTransaction();

			playlistTracks = this.playlist.getPlaylistTracks();

			int pos = (this.position != null ? this.position : playlistTracks.size());

			// TODO: this class is doing much more than querying, so it should probably be up a layer higher
			if (pos > playlistTracks.size() || pos < 0) {
				// TODO Change this to a different exception
			}

			for (final Track track : this.tracks) {
				PlaylistTrack playlistTrack = new PlaylistTrack();
				playlistTrack.setId(new PlaylistTrack.Id(this.playlist.getId(), track.getId()));
				playlistTrack.setPlaylist(this.playlist);
				playlistTrack.setTrack(track);
				playlistTracks.add(pos, playlistTrack);
			}

			session.update(this.playlist);

			this.db.commit();

		} finally {
			this.db.endTransaction();
		}

		return playlistTracks;
	}
}
