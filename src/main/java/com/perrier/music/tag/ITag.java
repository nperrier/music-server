package com.perrier.music.tag;

import java.awt.image.BufferedImage;

public interface ITag {

	// ITag parse(final File file) throws IOException;

	String getArtist();

	String getAlbum();

	String getAlbumArtist();

	String getTrack();

	Integer getNumber();

	Long getLength();

	String getYear();

	String getGenre();

	BufferedImage getCoverArt();

	// boolean hasCoverArt();
}
