package com.perrier.music.stream;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteSource;
import com.google.common.io.Files;
import com.perrier.music.entity.track.Track;

public class TrackStreamer {

	private static final Logger log = LoggerFactory.getLogger(TrackStreamer.class);

	private final Track track;

	public TrackStreamer(Track track) {
		this.track = track;
	}

	public void writeStream(OutputStream out) throws StreamException {

		File file = new File(this.track.getPath());

		try {

			ByteSource source = Files.asByteSource(file);
			source.copyTo(out);

		} catch (IOException e) {
			// throw new StreamException("An error occurred while streaming the track file: " + file, e);
			log.debug("An IOException occurred while streaming the track file: " + file, e);
		} finally {
			IOUtils.closeQuietly(out);
		}
	}
}
