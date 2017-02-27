package com.perrier.music.api;

public class TrackUpdateDto {

	String name;
	String artist;
	String albumArtist;
	String album;
	String genre;
	String coverArtUrl;
	String coverHash;
	String coverStorageKey;
	Integer number;
	String year;

	public TrackUpdateDto() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public String getAlbum() {
		return album;
	}

	public void setAlbum(String album) {
		this.album = album;
	}

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	public String getCoverArtUrl() {
		return coverArtUrl;
	}

	public void setCoverArtUrl(String coverArtUrl) {
		this.coverArtUrl = coverArtUrl;
	}

	public String getCoverHash() {
		return coverHash;
	}

	public void setCoverHash(String coverHash) {
		this.coverHash = coverHash;
	}

	public String getCoverStorageKey() {
		return coverStorageKey;
	}

	public void setCoverStorageKey(String coverStorageKey) {
		this.coverStorageKey = coverStorageKey;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getAlbumArtist() {
		return albumArtist;
	}

	public void setAlbumArtist(String albumArtist) {
		this.albumArtist = albumArtist;
	}
}
