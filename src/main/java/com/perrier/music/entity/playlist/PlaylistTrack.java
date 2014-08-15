package com.perrier.music.entity.playlist;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.perrier.music.entity.track.Track;

@Entity
@Table(name = "playlist_track")
// , uniqueConstraints = { @UniqueConstraint(columnNames = { "playlist_id", "track_id", "position" }) })
public class PlaylistTrack {

	@Embeddable
	public static final class Id implements Serializable {

		private static final long serialVersionUID = -6155645867629815745L;

		private Long playlistId;
		private Long trackId;

		public Id() {
		}

		public Id(long playlistId, long trackId) {
			this.setPlaylistId(playlistId);
			this.setTrackId(trackId);
		}

		@Column(name = "playlist_id")
		public Long getPlaylistId() {
			return this.playlistId;
		}

		public void setPlaylistId(Long playlistId) {
			this.playlistId = playlistId;
		}

		@Column(name = "track_id")
		public Long getTrackId() {
			return this.trackId;
		}

		public void setTrackId(Long trackId) {
			this.trackId = trackId;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((this.getPlaylistId() == null) ? 0 : this.getPlaylistId().hashCode());
			result = prime * result + ((this.getTrackId() == null) ? 0 : this.getTrackId().hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (!(obj instanceof Id)) {
				return false;
			}
			Id other = (Id) obj;
			if (this.getPlaylistId() == null) {
				if (other.getPlaylistId() != null) {
					return false;
				}
			} else if (!this.getPlaylistId().equals(other.getPlaylistId())) {
				return false;
			}
			if (this.getTrackId() == null) {
				if (other.getTrackId() != null) {
					return false;
				}
			} else if (!this.getTrackId().equals(other.getTrackId())) {
				return false;
			}
			return true;
		}

	}

	private Id id;
	private Playlist playlist;
	private Track track;
	private Integer position;

	@EmbeddedId
	public Id getId() {
		return this.id;
	}

	public void setId(Id id) {
		this.id = id;
	}

	@ManyToOne
	@JoinColumn(name = "playlist_id", insertable = false, updatable = false, nullable = false)
	public Playlist getPlaylist() {
		return this.playlist;
	}

	public void setPlaylist(Playlist playlist) {
		this.playlist = playlist;
	}

	@ManyToOne
	@JoinColumn(name = "track_id", insertable = false, updatable = false, nullable = false)
	public Track getTrack() {
		return this.track;
	}

	public void setTrack(Track track) {
		this.track = track;
	}

	@Column(name = "position", insertable = false, updatable = false, nullable = false)
	public Integer getPosition() {
		return this.position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

}
