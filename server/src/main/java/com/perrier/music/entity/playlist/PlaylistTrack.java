package com.perrier.music.entity.playlist;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.perrier.music.entity.track.Track;

@Entity
@Table(name = "playlist_track")
// , uniqueConstraints = { @UniqueConstraint(columnNames = { "playlist_id", "track_id", "position" }) })
public class PlaylistTrack {

	private Long id;
	private Playlist playlist;
	private Track track;
	private Integer position;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@ManyToOne
	@JoinColumn(name = "playlist_id", insertable = true, updatable = false, nullable = false)
	public Playlist getPlaylist() {
		return this.playlist;
	}

	public void setPlaylist(Playlist playlist) {
		this.playlist = playlist;
	}

	@ManyToOne
	@JoinColumn(name = "track_id", insertable = true, updatable = false, nullable = false)
	public Track getTrack() {
		return this.track;
	}

	public void setTrack(Track track) {
		this.track = track;
	}

	@Column(name = "position", insertable = true, updatable = true, nullable = true)
	public Integer getPosition() {
		return this.position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

}
