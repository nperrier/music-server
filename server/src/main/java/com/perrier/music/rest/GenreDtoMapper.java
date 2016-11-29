package com.perrier.music.rest;

import java.util.List;

import com.google.common.collect.Lists;
import com.perrier.music.api.GenreDto;
import com.perrier.music.entity.genre.Genre;

public class GenreDtoMapper {

	public static final GenreDto EMPTY_GENRE = new GenreDto();

	public static GenreDto build(Genre genre) {

		if (genre == null) {
			return EMPTY_GENRE;
		}

		GenreDto dto = new GenreDto();
		dto.setName(genre.getName());
		dto.setId(genre.getId());
		dto.setCreationDate(genre.getCreationDate());
		dto.setModificationDate(genre.getModificationDate());

		return dto;
	}

	public static List<GenreDto> build(List<Genre> genres) {

		List<GenreDto> dtos = Lists.newArrayListWithCapacity(genres.size());
		for (Genre genre : genres) {
			dtos.add(build(genre));
		}

		return dtos;
	}
}
