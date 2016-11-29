package com.perrier.music.api;

public class AlbumUpdateDto {

	private String name;
	private String artist;
	private String coverArtUrl;

	public AlbumUpdateDto() {
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

	public String getCoverArtUrl() {
		return coverArtUrl;
	}

	public void setCoverArtUrl(String coverArtUrl) {
		this.coverArtUrl = coverArtUrl;
	}

	@Override
	public String toString() {
		return "AlbumUpdateDto{" +
				"name='" + name + '\'' +
				", artist='" + artist + '\'' +
				", coverArtUrl='" + coverArtUrl + '\'' +
				'}';
	}
}
