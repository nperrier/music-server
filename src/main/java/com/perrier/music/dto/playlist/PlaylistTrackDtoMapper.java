package com.perrier.music.dto.playlist;

import java.util.List;

import com.google.common.collect.Lists;
import com.perrier.music.dto.track.TrackDtoMapper;
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
		dto.setPosition(playlistTrack.getPosition());
		dto.setPlaylistId(playlistTrack.getPlaylist().getId());
		dto.setTrackDto(TrackDtoMapper.build(playlistTrack.getTrack()));

		return dto;
	}

}
