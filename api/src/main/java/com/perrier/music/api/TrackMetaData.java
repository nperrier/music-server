package com.perrier.music.api;

public class TrackMetaData {

	private Long id;
	private String name;
	private Integer number;
	private String year;
	private Long length;
	private String artist;
	private String albumArtist;
	private String album;
	private String genre;
	private String audioHash;
	private String audioStorageKey;
	private String audioUrl;
	private String coverHash;
	private String coverStorageKey;
	private String coverUrl;
	private Long fileModificationDate;
	private Boolean edited = false;

	// transient fields
	private transient boolean indexed;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public Long getLength() {
		return length;
	}

	public void setLength(Long length) {
		this.length = length;
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

	public String getAlbumArtist() {
		return albumArtist;
	}

	public void setAlbumArtist(String albumArtist) {
		this.albumArtist = albumArtist;
	}

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	public Long getFileModificationDate() {
		return fileModificationDate;
	}

	public void setFileModificationDate(Long fileModificationDate) {
		this.fileModificationDate = fileModificationDate;
	}

	public boolean isIndexed() {
		return indexed;
	}

	public void setIndexed(boolean indexed) {
		this.indexed = indexed;
	}

	public String getAudioHash() {
		return audioHash;
	}

	public void setAudioHash(String audioHash) {
		this.audioHash = audioHash;
	}

	public String getCoverHash() {
		return coverHash;
	}

	public void setCoverHash(String coverHash) {
		this.coverHash = coverHash;
	}

	public String getAudioUrl() {
		return audioUrl;
	}

	public void setAudioUrl(String audioUrl) {
		this.audioUrl = audioUrl;
	}

	public String getCoverUrl() {
		return coverUrl;
	}

	public void setCoverUrl(String coverUrl) {
		this.coverUrl = coverUrl;
	}

	public String getAudioStorageKey() {
		return audioStorageKey;
	}

	public void setAudioStorageKey(String audioStorageKey) {
		this.audioStorageKey = audioStorageKey;
	}

	public String getCoverStorageKey() {
		return coverStorageKey;
	}

	public void setCoverStorageKey(String coverStorageKey) {
		this.coverStorageKey = coverStorageKey;
	}

	public Boolean getEdited() {
		return edited;
	}

	public void setEdited(Boolean edited) {
		this.edited = edited;
	}

	@Override
	public String toString() {
		return "TrackMetaData{" +
				"id=" + id +
				", name='" + name + '\'' +
				", number=" + number +
				", year='" + year + '\'' +
				", length=" + length +
				", artist='" + artist + '\'' +
				", albumArtist='" + albumArtist + '\'' +
				", album='" + album + '\'' +
				", genre='" + genre + '\'' +
				", audioHash='" + audioHash + '\'' +
				", audioStorageKey='" + audioStorageKey + '\'' +
				", audioUrl='" + audioUrl + '\'' +
				", coverHash='" + coverHash + '\'' +
				", coverStorageKey='" + coverStorageKey + '\'' +
				", coverUrl='" + coverUrl + '\'' +
				", fileModificationDate=" + fileModificationDate +
				", edited=" + edited +
				", indexed=" + indexed +
				'}';
	}
}
