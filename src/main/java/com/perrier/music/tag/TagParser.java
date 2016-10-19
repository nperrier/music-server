package com.perrier.music.tag;

import java.io.File;
import java.io.IOException;

import org.jaudiotagger.audio.generic.Utils;

public class TagParser implements ITagParser {

	public ITag parseTag(File file) throws TagException {

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
}
