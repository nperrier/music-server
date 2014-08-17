package com.perrier.music.dto.playlist;

import com.perrier.music.dto.track.TrackDto;

public class PlaylistTrackDto {

	private Integer position;
	private Long playlistId;
	private TrackDto track;

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
