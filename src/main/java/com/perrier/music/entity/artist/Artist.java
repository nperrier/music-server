package com.perrier.music.entity.artist;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.perrier.music.entity.AbstractAuditableEntity;
import com.perrier.music.entity.album.Album;
import com.perrier.music.entity.track.Track;

@Entity
@Table(name = "artist")
// @FetchProfiles({
// @FetchProfile(name = "artist-with-tracks", fetchOverrides = {
// @FetchProfile.FetchOverride(entity = Artist.class, association = "tracks", mode = FetchMode.JOIN)
// }),
// @FetchProfile(name = "artist-with-albums", fetchOverrides = {
// @FetchProfile.FetchOverride(entity = Artist.class, association = "albums", mode = FetchMode.JOIN)
// })
// })
public class Artist extends AbstractAuditableEntity {

	public static final String UNKNOWN_ARTIST = "Unknown Artist";

	private Long id;
	private String name;
	private List<Track> tracks;
	private List<Album> albums;

	// private CoverArt coverArt;

	public Artist() {
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(nullable = false, unique = true, length = 255)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	// TODO I don't want all the artist's Tracks loaded every time I access an Artist,
	// but it seems that Jersey/Jackson don't work properly with LAZY fetching
	// @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "artist_id")
	public List<Track> getTracks() {
		return this.tracks;
	}

	public void setTracks(List<Track> tracks) {
		this.tracks = tracks;
	}

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "artist_id")
	public List<Album> getAlbums() {
		return this.albums;
	}

	public void setAlbums(List<Album> albums) {
		this.albums = albums;
	}

	@Override
	public String toString() {
		return "Artist [id=" + this.id + ", name=" + this.name + "]";
	}

}
