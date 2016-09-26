package com.perrier.music.dto.search;

import java.util.Collections;
import java.util.List;

import com.perrier.music.dto.album.AlbumDto;
import com.perrier.music.dto.artist.ArtistDto;
import com.perrier.music.dto.track.TrackDto;

public class SearchResultsDto {

	private List<AlbumDto> albums = Collections.emptyList();
	private Long albumsTotal;
	private List<ArtistDto> artists = Collections.emptyList();
	private Long artistsTotal;
	private List<TrackDto> tracks = Collections.emptyList();
	private Long tracksTotal;

	public SearchResultsDto() {
	}

	public void setAlbums(List<AlbumDto> albums) {
		this.albums = albums;
	}

	public List<AlbumDto> getAlbums() {
		return this.albums;
	}

	public void setArtists(List<ArtistDto> artists) {
		this.artists = artists;
	}

	public List<ArtistDto> getArtists() {
		return this.artists;
	}

	public void setTracks(List<TrackDto> tracks) {
		this.tracks = tracks;
	}

	public List<TrackDto> getTracks() {
		return this.tracks;
	}

	public void setAlbumsTotal(long albumsTotal) {
		this.albumsTotal = albumsTotal;
	}

	public void setArtistsTotal(long artistsTotal) {
		this.artistsTotal = artistsTotal;
	}

	public void setTracksTotal(long tracksTotal) {
		this.tracksTotal = tracksTotal;
	}

	public Long getAlbumsTotal() {
		return albumsTotal;
	}

	public Long getArtistsTotal() {
		return artistsTotal;
	}

	public Long getTracksTotal() {
		return tracksTotal;
	}
}
