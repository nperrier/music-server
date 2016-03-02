package com.perrier.music.dto.playlist;

import com.perrier.music.dto.track.TrackDto;

public class PlaylistTrackDto {

	private Long id;
	private Integer position;
	private Long playlistId;
	private TrackDto track;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getPosition() {
		return this.position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	public Long getPlaylistId() {
		return this.playlistId;
	}

	public void setPlaylistId(Long playlistId) {
		this.playlistId = playlistId;
	}

	public TrackDto getTrack() {
		return this.track;
	}

	public void setTrack(TrackDto trackDto) {
		this.track = trackDto;
	}
}
