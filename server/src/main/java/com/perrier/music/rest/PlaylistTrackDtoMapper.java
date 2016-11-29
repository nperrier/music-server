package com.perrier.music.rest;

import java.util.List;

import com.google.common.collect.Lists;
import com.perrier.music.api.PlaylistTrackDto;
import com.perrier.music.entity.playlist.PlaylistTrack;

public class PlaylistTrackDtoMapper {

	private PlaylistTrackDtoMapper() {
	}

	public static List<PlaylistTrackDto> build(List<PlaylistTrack> playlistTracks) {

		List<PlaylistTrackDto> dtos = Lists.newArrayListWithCapacity(playlistTracks.size());
		for (PlaylistTrack track : playlistTracks) {
			dtos.add(build(track));
		}

		return dtos;
	}

	public static PlaylistTrackDto build(PlaylistTrack playlistTrack) {

		PlaylistTrackDto dto = new PlaylistTrackDto();
		dto.setId(playlistTrack.getId());
		dto.setPosition(playlistTrack.getPosition());
		dto.setPlaylistId(playlistTrack.getPlaylist().getId());
		dto.setTrack(TrackDtoMapper.build(playlistTrack.getTrack()));

		return dto;
	}
}
