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
import com.perrier.music.entity.library.Library;

@Entity
@Table(name = "track", uniqueConstraints = { @UniqueConstraint(columnNames = { "name", "artist_id", "album_id" }) })
public class Track extends AbstractAuditableEntity {

	public static final String UNKNOWN_TRACK = "Unknown Track";

	private Long id;
	private String name;
	private Integer number;
	private String path;
	private Date year;
	private Long length;
	private Artist artist;
	private Album album;
	private Genre genre;
	private Library library;
	private String coverArt;
	private Date fileModificationDate;

	public Track() {
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

	@Column(nullable = false, length = 255)
	public String getPath() {
		return this.path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	@Column(nullable = true)
	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = true)
	public Date getYear() {
		return year;
	}

	public void setYear(Date year) {
		this.year = year;
	}

	public void setArtist(Artist artist) {
		this.artist = artist;
	}
	
	@Column(nullable = false)
	public Long getLength() {
		return length;
	}

	public void setLength(Long length) {
		this.length = length;
	}

	@ManyToOne
	@JoinColumn(name = "artist_id", nullable = false, insertable = true, updatable = true)
	public Artist getArtist() {
		return this.artist;
	}

	@ManyToOne
	@JoinColumn(name = "album_id", nullable = false, insertable = true, updatable = true)
	public Album getAlbum() {
		return album;
	}

	public void setAlbum(Album album) {
		this.album = album;
	}

	@ManyToOne
	@JoinColumn(name = "library_id", nullable = false, insertable = true, updatable = true)
	public Library getLibrary() {
		return library;
	}

	public void setLibrary(Library library) {
		this.library = library;
	}

	@ManyToOne
	@JoinColumn(name = "genre_id", insertable = true, updatable = true)
	public Genre getGenre() {
		return genre;
	}

	public void setGenre(Genre genre) {
		this.genre = genre;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "file_modification_date", nullable = false)
	public Date getFileModificationDate() {
		return fileModificationDate;
	}

	public void setFileModificationDate(Date fileModificationDate) {
		this.fileModificationDate = fileModificationDate;
	}

	@Column(name = "cover_art", nullable = true, length = 255)
	public String getCoverArt() {
		return coverArt;
	}

	public void setCoverArt(String coverArt) {
		this.coverArt = coverArt;
	}
}
