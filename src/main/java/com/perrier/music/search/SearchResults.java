package com.perrier.music.search;

import java.util.Collections;
import java.util.List;

import com.perrier.music.entity.album.Album;
import com.perrier.music.entity.artist.Artist;
import com.perrier.music.entity.track.Track;

public class SearchResults {

	private List<Album> albums = Collections.emptyList();
	private long albumsTotal;
	private List<Artist> artists = Collections.emptyList();
	private long artistsTotal;
	private List<Track> tracks = Collections.emptyList();
	private long tracksTotal;

	public SearchResults() {
	}

	public void setAlbums(List<Album> albums) {
		this.albums = albums;
	}

	public List<Album> getAlbums() {
		return this.albums;
	}

	public void setArtists(List<Artist> artists) {
		this.artists = artists;
	}

	public List<Artist> getArtists() {
		return this.artists;
	}

	public void setTracks(List<Track> tracks) {
		this.tracks = tracks;
	}

	public List<Track> getTracks() {
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

	public long getAlbumsTotal() {
		return albumsTotal;
	}

	public long getArtistsTotal() {
		return artistsTotal;
	}

	public long getTracksTotal() {
		return tracksTotal;
	}
}
