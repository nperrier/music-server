package com.perrier.music.dto.playlist;

import java.util.List;

import com.google.common.collect.Lists;
import com.perrier.music.entity.playlist.Playlist;

public class PlaylistDtoMapper {

	private PlaylistDtoMapper() {
	}

	public static PlaylistDto build(Playlist playlist) {

		PlaylistDto dto = new PlaylistDto();
		dto.setId(playlist.getId());
		dto.setName(playlist.getName());
		// dto.setPlaylistTracks(PlaylistTrackDtoMapper.build(playlist.getPlaylistTracks()));
		dto.setModificationDate(playlist.getModificationDate());
		dto.setCreationDate(playlist.getCreationDate());

		return dto;
	}

	public static List<PlaylistDto> build(List<Playlist> playlists) {

		List<PlaylistDto> dtos = Lists.newArrayListWithCapacity(playlists.size());
		for (Playlist playlist : playlists) {
			dtos.add(build(playlist));
		}

		return dtos;
	}
}
