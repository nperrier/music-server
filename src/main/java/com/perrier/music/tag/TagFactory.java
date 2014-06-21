package com.perrier.music.tag;

import java.io.File;
import java.io.IOException;

import org.jaudiotagger.audio.generic.Utils;

public class TagFactory {

	public static ITag parseTag(File file) throws TagException {
		
		ITag tag = null;

		String extension = Utils.getExtension(file);
		try {
			if ("mp3".equals(extension)) {
				tag = Mp3Tag.parse(file);
			} else {
				throw new TagException("Encoding not supported: " + extension);
			}
		} catch (IOException e) {
			throw new TagException("Error parsing tag, file: " + file);
		}

		return tag;
	}
	
	
	/**
	 *  Track cannot be null. Set it to the file name by default
	 *  
	 * @param filename file name of track
	 * @param rawTrack track name from tag
	 * @return rawTrack if not null, else filename
	 */
	protected static String setTrack(final String filename, final String rawTrack) {

		return (rawTrack == null) ? filename : rawTrack;
	}
}
