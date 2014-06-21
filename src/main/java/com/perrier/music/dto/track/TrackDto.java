package com.perrier.music.dto.track;

import java.util.Date;

import com.perrier.music.dto.AuditableDto;
import com.perrier.music.dto.album.AlbumDto;
import com.perrier.music.dto.artist.ArtistDto;
import com.perrier.music.dto.genre.GenreDto;

public class TrackDto extends AuditableDto {

	private Long id;
	private String name;
	private Integer number;
	// private String path;
	private Long length;
	private String coverArtUrl;
	private String streamUrl;
	private Date year;
	private Date fileModificationDate;

	private ArtistDto artist;
	private AlbumDto album;
	private GenreDto genre;

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getNumber() {
		return this.number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public Date getYear() {
		return this.year;
	}

	public void setYear(Date year) {
		this.year = year;
	}

	public Long getLength() {
		return this.length;
	}

	public void setLength(Long length) {
		this.length = length;
	}

	public Date getFileModificationDate() {
		return this.fileModificationDate;
	}

	public void setFileModificationDate(Date fileModificationDate) {
		this.fileModificationDate = fileModificationDate;
	}

	public ArtistDto getArtist() {
		return this.artist;
	}

	public void setArtist(ArtistDto artist) {
		this.artist = artist;
	}

	public AlbumDto getAlbum() {
		return this.album;
	}

	public void setAlbum(AlbumDto album) {
		this.album = album;
	}

	public GenreDto getGenre() {
		return this.genre;
	}

	public void setGenre(GenreDto genre) {
		this.genre = genre;
	}

	public String getCoverArtUrl() {
		return this.coverArtUrl;
	}

	public void setCoverArtUrl(String coverArtUrl) {
		this.coverArtUrl = coverArtUrl;
	}

	public String getStreamUrl() {
		return this.streamUrl;
	}

	public void setStreamUrl(String streamUrl) {
		this.streamUrl = streamUrl;
	}

}
