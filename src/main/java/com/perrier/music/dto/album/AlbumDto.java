package com.perrier.music.dto.album;

import com.perrier.music.dto.AuditableDto;
import com.perrier.music.dto.artist.ArtistDto;

public class AlbumDto extends AuditableDto {

	private Long id;
	private String name;
	private ArtistDto artist;
	// private GenreDto genre;
	private String year;
	private String coverArtUrl;

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

	public ArtistDto getArtist() {
		return artist;
	}

	public void setArtist(ArtistDto artist) {
		this.artist = artist;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getCoverArtUrl() {
		return coverArtUrl;
	}

	public void setCoverArtUrl(String coverArtUrl) {
		this.coverArtUrl = coverArtUrl;
	}
}
