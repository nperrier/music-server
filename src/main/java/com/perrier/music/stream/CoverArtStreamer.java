package com.perrier.music.stream;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;

import com.google.common.io.ByteSource;
import com.google.common.io.Files;

public class CoverArtStreamer {

	private final File file;
	
	public CoverArtStreamer(File file) {
		this.file = file;
	}
	
	public void writeStream(OutputStream out) throws StreamException {
		
		try {
			
			ByteSource source = Files.asByteSource(file);
			source.copyTo(out);
			
		} catch (IOException e) {
			throw new StreamException("An error occurred while sending the cover file: " + file, e);
		}
		finally {
			IOUtils.closeQuietly(out);
		}
	}
}
