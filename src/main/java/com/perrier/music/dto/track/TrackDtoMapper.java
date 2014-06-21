package com.perrier.music.dto.track;

import java.util.List;

import com.google.common.collect.Lists;
import com.perrier.music.dto.album.AlbumDtoMapper;
import com.perrier.music.dto.artist.ArtistDtoMapper;
import com.perrier.music.dto.genre.GenreDtoMapper;
import com.perrier.music.entity.track.Track;

public class TrackDtoMapper {

	private TrackDtoMapper() {
	}

	public static TrackDto build(Track track) {

		TrackDto dto = new TrackDto();
		dto.setId(track.getId());
		dto.setName(track.getName());
		dto.setNumber(track.getNumber());
		dto.setYear(track.getYear());
		dto.setLength(track.getLength());
		dto.setArtist(ArtistDtoMapper.build(track.getArtist()));
		dto.setAlbum(AlbumDtoMapper.build(track.getAlbum()));
		dto.setGenre(GenreDtoMapper.build(track.getGenre()));
		dto.setCoverArtUrl("/api/cover/track/" + track.getId());
		dto.setStreamUrl("/api/stream/" + track.getId());
		dto.setFileModificationDate(track.getFileModificationDate());
		dto.setCreationDate(track.getCreationDate());
		dto.setModificationDate(track.getModificationDate());

		return dto;
	}

	public static List<TrackDto> build(List<Track> tracks) {

		List<TrackDto> dtos = Lists.newArrayListWithCapacity(tracks.size());
		for (Track track : tracks) {
			dtos.add(build(track));
		}

		return dtos;
	}
}
