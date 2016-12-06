package com.perrier.music.rest.stream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.io.IOUtils;

public class HttpStreamer implements StreamingOutput {

	private final InputStream inputStream;

	public HttpStreamer(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	@Override
	public void write(OutputStream out) throws IOException, WebApplicationException {
		try {
			IOUtils.copy(this.inputStream, out);
		} finally {
			IOUtils.closeQuietly(this.inputStream);
			IOUtils.closeQuietly(out);
		}
	}
}
