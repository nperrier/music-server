package com.perrier.music.tag;

import java.awt.image.BufferedImage;
import java.util.Date;

public interface ITag {

//	ITag parse(final File file) throws IOException;

	String getArtist();

	String getAlbum();

	String getAlbumArtist();

	String getTrack();

	Integer getNumber();
	
	Long getLength();

	Date getYear();

	String getGenre();

	BufferedImage getCoverArt();

//	boolean hasCoverArt();
}
