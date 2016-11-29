package com.perrier.music.rest;

import java.util.List;

import com.google.common.collect.Lists;
import com.perrier.music.api.TrackDto;
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
		dto.setCoverArtUrl(track.getCoverUrl());
		dto.setStreamUrl(track.getAudioUrl());
		dto.setDownloadUrl("/api/track/download/" + track.getId());
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
