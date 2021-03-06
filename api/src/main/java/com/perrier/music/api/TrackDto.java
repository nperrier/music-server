package com.perrier.music.api;

import java.util.Date;

import com.perrier.music.api.AuditableDto;
import com.perrier.music.api.AlbumDto;
import com.perrier.music.api.ArtistDto;
import com.perrier.music.api.GenreDto;

public class TrackDto extends AuditableDto {

	private Long id;
	private String name;
	private Integer number;
	// private String path;
	private Long length;
	private String coverArtUrl;
	private String streamUrl;
	private String downloadUrl;
	private String year;
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

	public String getYear() {
		return this.year;
	}

	public void setYear(String year) {
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

	public String getDownloadUrl() {
		return downloadUrl;
	}

	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}
}
