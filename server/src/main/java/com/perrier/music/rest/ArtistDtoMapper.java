package com.perrier.music.rest;

import java.util.List;

import com.google.common.collect.Lists;
import com.perrier.music.api.ArtistDto;
import com.perrier.music.entity.artist.Artist;

public class ArtistDtoMapper {

	private static final ArtistDto EMPTY_ARTIST = new ArtistDto();

	private ArtistDtoMapper() {
	}

	public static ArtistDto build(Artist artist) {

		if (artist == null) {
			return EMPTY_ARTIST;
		}

		ArtistDto dto = new ArtistDto();
		dto.setId(artist.getId());
		dto.setName(artist.getName());
		dto.setCreationDate(artist.getCreationDate());
		dto.setModificationDate(artist.getModificationDate());

		return dto;
	}

	public static List<ArtistDto> build(List<Artist> artists) {

		List<ArtistDto> dtos = Lists.newArrayListWithCapacity(artists.size());
		for (Artist artist : artists) {
			dtos.add(build(artist));
		}

		return dtos;
	}
}
