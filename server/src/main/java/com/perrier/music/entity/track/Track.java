package com.perrier.music.entity.track;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import com.perrier.music.entity.AbstractAuditableEntity;
import com.perrier.music.entity.album.Album;
import com.perrier.music.entity.artist.Artist;
import com.perrier.music.entity.genre.Genre;

@Entity
@Table(name = "track", uniqueConstraints = { @UniqueConstraint(columnNames = { "name", "artist_id", "album_id" }) })
public class Track extends AbstractAuditableEntity {

	private Long id;
	private String name;
	private Integer number;
	private String year;
	private Long length;
	private Artist artist;
	private Artist albumArtist;
	private Album album;
	private Genre genre;
	private String audioHash;
	private String audioStorageKey;
	private String audioUrl;
	private String coverHash;
	private String coverStorageKey;
	private String coverUrl;
	private Date fileModificationDate;
	private Boolean edited = false;

	public Track() {
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

	@Column(name = "number")
	public Integer getNumber() {
		return this.number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	@Column(name = "year", length = 4)
	public String getYear() {
		return this.year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	@Column(name = "length", nullable = false)
	public Long getLength() {
		return this.length;
	}

	public void setLength(Long length) {
		this.length = length;
	}

	@ManyToOne
	@JoinColumn(name = "artist_id")
	public Artist getArtist() {
		return this.artist;
	}

	public void setArtist(Artist artist) {
		this.artist = artist;
	}

	@ManyToOne
	@JoinColumn(name = "album_artist_id")
	public Artist getAlbumArtist() {
		return albumArtist;
	}

	public void setAlbumArtist(Artist albumArtist) {
		this.albumArtist = albumArtist;
	}

	@ManyToOne
	@JoinColumn(name = "album_id")
	public Album getAlbum() {
		return this.album;
	}

	public void setAlbum(Album album) {
		this.album = album;
	}

	@ManyToOne
	@JoinColumn(name = "genre_id")
	public Genre getGenre() {
		return this.genre;
	}

	public void setGenre(Genre genre) {
		this.genre = genre;
	}

	@Column(name = "audio_hash", nullable = false)
	public String getAudioHash() {
		return audioHash;
	}

	public void setAudioHash(String audioHash) {
		this.audioHash = audioHash;
	}

	@Column(name = "cover_hash")
	public String getCoverHash() {
		return coverHash;
	}

	public void setCoverHash(String coverHash) {
		this.coverHash = coverHash;
	}

	@Column(name = "audio_storage_key", nullable = false)
	public String getAudioStorageKey() {
		return audioStorageKey;
	}

	public void setAudioStorageKey(String audioStorageKey) {
		this.audioStorageKey = audioStorageKey;
	}

	@Column(name = "cover_storage_key")
	public String getCoverStorageKey() {
		return coverStorageKey;
	}

	public void setCoverStorageKey(String coverStorageKey) {
		this.coverStorageKey = coverStorageKey;
	}

	@Column(name = "audio_url", nullable = false)
	public String getAudioUrl() {
		return audioUrl;
	}

	public void setAudioUrl(String audioUrl) {
		this.audioUrl = audioUrl;
	}

	@Column(name = "cover_url")
	public String getCoverUrl() {
		return coverUrl;
	}

	public void setCoverUrl(String coverUrl) {
		this.coverUrl = coverUrl;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "file_modification_date", nullable = false)
	public Date getFileModificationDate() {
		return this.fileModificationDate;
	}

	public void setFileModificationDate(Date fileModificationDate) {
		this.fileModificationDate = fileModificationDate;
	}

	@Column(name = "edited", nullable = false)
	public Boolean getEdited() {
		return edited;
	}

	public void setEdited(Boolean edited) {
		this.edited = edited;
	}

	@Override
	public String toString() {
		return "Track{" +
				"id=" + id +
				", name='" + name + '\'' +
				", number=" + number +
				", year='" + year + '\'' +
				", length=" + length +
				", artist=" + artist +
				", albumArtist=" + albumArtist +
				", album=" + album +
				", genre=" + genre +
				", audioHash='" + audioHash + '\'' +
				", audioStorageKey='" + audioStorageKey + '\'' +
				", audioUrl='" + audioUrl + '\'' +
				", coverHash='" + coverHash + '\'' +
				", coverStorageKey='" + coverStorageKey + '\'' +
				", coverUrl='" + coverUrl + '\'' +
				", fileModificationDate=" + fileModificationDate +
				", edited=" + edited +
				'}';
	}
}
