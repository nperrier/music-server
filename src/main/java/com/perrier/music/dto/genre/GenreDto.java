package com.perrier.music.dto.genre;

import com.perrier.music.dto.AuditableDto;

public class GenreDto extends AuditableDto {

	public Long id;
	public String name;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
