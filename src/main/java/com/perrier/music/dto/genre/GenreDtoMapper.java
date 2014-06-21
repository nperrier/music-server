package com.perrier.music.dto.genre;

import com.perrier.music.entity.genre.Genre;

public class GenreDtoMapper {

	
	public static GenreDto build(Genre genre) {
		
		GenreDto dto = new GenreDto();
		dto.setName(genre.getName());
		dto.setCreationDate(genre.getCreationDate());
		dto.setModificationDate(genre.getModificationDate());
		
		return dto;
	}
}
