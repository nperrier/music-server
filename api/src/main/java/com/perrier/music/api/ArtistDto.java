package com.perrier.music.api;

import com.perrier.music.api.AuditableDto;

public class ArtistDto extends AuditableDto {

	private Long id;
	private String name;

	// private CoverArtDto coverArt;

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
