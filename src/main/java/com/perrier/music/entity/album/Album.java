package com.perrier.music.entity.album;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.perrier.music.entity.AbstractAuditableEntity;
import com.perrier.music.entity.artist.Artist;
import com.perrier.music.entity.track.Track;

@Entity
@Table(name = "album", uniqueConstraints = { @UniqueConstraint(columnNames = { "name", "artist_id" }) })
// @FetchProfile(name = "album-with-tracks", fetchOverrides = {
// @FetchProfile.FetchOverride(entity = Album.class, association = "tracks", mode = FetchMode.JOIN)
// })
public class Album extends AbstractAuditableEntity {

	private Long id;
	private String name;
	private Artist artist;
	private List<Track> tracks;
	// private Genre genre;
	private String year;
	private String coverArt;

	public Album() {
	}

	/**
	 * Creates a shallow copy of this Album. This is really only needed for deleting an album.
	 * 
	 * @return
	 */
	public static Album copy(Album a) {
		if (a == null) {
			return null;
		}

		Album album = new Album();
		album.setId(a.getId());
		album.setName(a.getName());
		album.setYear(a.getYear());
		album.setArtist(a.getArtist());
		album.setCoverArt(a.getCoverArt());
		album.setTracks(a.getTracks());
		album.setCreationDate(a.getCreationDate());
		album.setModificationDate(a.getModificationDate());

		return album;
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

	@Column(nullable = false, length = 255)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setArtist(Artist artist) {
		this.artist = artist;
	}

	@ManyToOne
	@JoinColumn(name = "artist_id", insertable = true, updatable = true)
	public Artist getArtist() {
		return this.artist;
	}

	// TODO I don't want all the album's Tracks loaded every time I access an Album,
	// but it seems that Jersey/Jackson don't work properly with LAZY fetching
	// @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "album_id")
	public List<Track> getTracks() {
		return this.tracks;
	}

	public void setTracks(List<Track> tracks) {
		this.tracks = tracks;
	}

	// @ManyToOne
	// @JoinColumn(name = "genre_id", nullable = true, insertable = true, updatable = true)
	// public Genre getGenre() {
	// return genre;
	// }
	//
	// public void setGenre(Genre genre) {
	// this.genre = genre;
	// }

	public String getYear() {
		return this.year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	@Column(name = "cover_art", nullable = true, length = 255)
	public String getCoverArt() {
		return this.coverArt;
	}

	public void setCoverArt(String coverArt) {
		this.coverArt = coverArt;
	}

	@Override
	public String toString() {
		return "Album [id=" + this.id + ", name=" + this.name + ", year=" + this.year + ", coverArt=" + this.coverArt
				+ ", creationDate=" + this.creationDate + ", modificationDate=" + this.modificationDate + "]";
	}

}
