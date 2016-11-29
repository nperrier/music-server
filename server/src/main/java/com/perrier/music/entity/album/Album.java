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
import javax.persistence.OrderBy;
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
	private String coverHash;
	private String coverStorageKey;
	private String coverUrl;

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
		album.setCoverHash(a.getCoverHash());
		album.setTracks(a.getTracks());
		album.setCreationDate(a.getCreationDate());
		album.setModificationDate(a.getModificationDate());

		return album;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "name", nullable = false)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@ManyToOne
	@JoinColumn(name = "artist_id")
	public Artist getArtist() {
		return this.artist;
	}

	public void setArtist(Artist artist) {
		this.artist = artist;
	}

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "album_id")
	@OrderBy("number")
	public List<Track> getTracks() {
		return this.tracks;
	}

	public void setTracks(List<Track> tracks) {
		this.tracks = tracks;
	}

	// @ManyToOne
	// @JoinColumn(name = "genre_id")
	// public Genre getGenre() {
	// return genre;
	// }
	//
	// public void setGenre(Genre genre) {
	// this.genre = genre;
	// }

	@Column(name = "year")
	public String getYear() {
		return this.year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	@Column(name = "cover_hash")
	public String getCoverHash() {
		return this.coverHash;
	}

	public void setCoverHash(String coverHash) {
		this.coverHash = coverHash;
	}

	@Column(name = "cover_storage_key")
	public String getCoverStorageKey() {
		return coverStorageKey;
	}

	public void setCoverStorageKey(String coverStorageKey) {
		this.coverStorageKey = coverStorageKey;
	}

	@Column(name = "cover_url")
	public String getCoverUrl() {
		return coverUrl;
	}

	public void setCoverUrl(String coverUrl) {
		this.coverUrl = coverUrl;
	}

	@Override
	public String toString() {
		return "Album{" +
				"id=" + id +
				", name='" + name + '\'' +
				", artist=" + artist +
				", year='" + year + '\'' +
				", coverHash='" + coverHash + '\'' +
				", coverStorageKey='" + coverStorageKey + '\'' +
				", coverUrl='" + coverUrl + '\'' +
				'}';
	}
}
