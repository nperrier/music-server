package com.perrier.music.rest.stream;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;

import com.google.common.io.ByteSource;
import com.google.common.io.Files;
import org.apache.commons.io.IOUtils;

public class RangeStreamer implements StreamingOutput {

	private final long offset;
	private final long length;
	private final File file;

	public RangeStreamer(long offset, long length, File file) {
		this.offset = offset;
		this.length = length;
		this.file = file;
	}

	@Override
	public void write(OutputStream out) throws IOException, WebApplicationException {
		try {
			ByteSource source = Files.asByteSource(this.file);
			source.slice(offset, length).copyTo(out);
		} finally {
			IOUtils.closeQuietly(out);
		}
	}
}
