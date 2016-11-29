package com.perrier.music.api;

import com.perrier.music.api.AuditableDto;

public class PlaylistDto extends AuditableDto {

	private Long id;
	private String name;

	// private List<PlaylistTrackDto> playlistTracks;

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	// public void setPlaylistTracks(List<PlaylistTrackDto> playlistTracks) {
	// this.playlistTracks = playlistTracks;
	// }
	//
	// public List<PlaylistTrackDto> getPlaylistTracks() {
	// return this.playlistTracks;
	// }
}
