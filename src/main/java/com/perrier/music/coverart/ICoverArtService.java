package com.perrier.music.coverart;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import com.perrier.music.coverart.CoverArtService.Type;

public interface ICoverArtService {

	BufferedImage getCover(String path) throws IOException;

	/**
	 * Write the cover art image to a file in the covers cache directory
	 *
	 * @param image cover art image
	 * @return SHA string representing the file name of the cover image with extension
	 * @throws IOException
	 * @throws CoverArtException
	 */
	String cacheCoverArt(BufferedImage image) throws IOException, CoverArtException;

	boolean isCached(String path) throws IOException;

	File getCoverFile(Type type, Long id) throws CoverArtException;

}
