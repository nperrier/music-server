package com.perrier.music.api;

import com.perrier.music.api.AuditableDto;

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
