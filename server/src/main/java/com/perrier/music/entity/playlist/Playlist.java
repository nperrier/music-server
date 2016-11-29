package com.perrier.music.entity.playlist;

import java.util.List;
import javax.persistence.*;

import com.perrier.music.entity.AbstractAuditableEntity;

@Entity
@Table(name = "playlist", uniqueConstraints = {@UniqueConstraint(columnNames = {"name"})})
public class Playlist extends AbstractAuditableEntity {

	// Use the id of 1 to represent the player's playlist
	// private static final long DEFAULT_PLAYLIST_ID = 1;

	private Long id;
	private String name;
	private List<PlaylistTrack> playlistTracks;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(nullable = false, length = 255)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@OneToMany(mappedBy = "playlist", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	// @OneToMany(mappedBy = "playlist", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@OrderColumn(name = "position")
	public List<PlaylistTrack> getPlaylistTracks() {
		return this.playlistTracks;
	}

	public void setPlaylistTracks(List<PlaylistTrack> playlistTracks) {
		this.playlistTracks = playlistTracks;
	}

}
