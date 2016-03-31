package com.perrier.music.rest.resource;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.ws.rs.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.sun.jersey.api.ParamException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.perrier.music.db.DBException;
import com.perrier.music.entity.track.Track;
import com.perrier.music.entity.track.TrackProvider;
import com.perrier.music.rest.stream.FileStreamer;
import com.perrier.music.rest.stream.RangeStreamer;
import com.perrier.music.server.EntityNotFoundException;

@Path("api/stream")
public class StreamResource {

	public static final Logger log = LoggerFactory.getLogger(StreamResource.class);

	@Inject
	private TrackProvider trackProvider;

	@GET
	@Path("{id}")
	@Produces({"audio/mpeg", "application/json"})
	public Response get(@HeaderParam("Range") String rangeHeader, @PathParam("id") Long id) throws DBException, IOException {

		Track track = this.trackProvider.findById(id);

		if (track == null) {
			throw new EntityNotFoundException("track not found");
		}

		final File trackFile = new File(track.getPath());

		if (rangeHeader != null) {
			Range range = processRangeRequest(rangeHeader, trackFile);
			final RangeStreamer stream = new RangeStreamer(range.getStart(), range.getLength(), trackFile);
			final String responseRange = String.format("bytes %d-%d/%d", range.getStart(), range.getEnd(), trackFile.length());

			return Response.ok(stream).status(206).header("Accept-Ranges", "bytes").header("Content-Range", responseRange).header(HttpHeaders.CONTENT_LENGTH, range.getLength()).header(HttpHeaders.LAST_MODIFIED, new Date(trackFile.lastModified())).build();
		} else {
			FileStreamer stream = new FileStreamer(trackFile);

			return Response.ok(stream).type("audio/mpeg").header(HttpHeaders.CONTENT_LENGTH, trackFile.length()).build();
		}

	}

	/**
	 * Process range request headers to seek to positions in the audio stream
	 * <p>
	 * GET /2390/2253727548_a413c88ab3_s.jpg HTTP/1.1
	 * Host: farm3.static.flickr.com
	 * Range: bytes=1000-
	 * <p>
	 * HTTP/1.0 206 Partial Content
	 * <p>
	 * Content-Length: 2980
	 * Content-Range: bytes 1000-3979/3980
	 */
	private static Range processRangeRequest(String rangeHeader, File trackFile) throws IOException, ParamException.HeaderParamException {

		final Pattern pattern = Pattern.compile("bytes=(\\d+)-(\\d+)?");
		final Matcher matcher = pattern.matcher(rangeHeader);

		if (!matcher.matches()) {
			log.error("Bad \"Range\" header: {}", rangeHeader);
			throw new WebApplicationException(416);
		}


		try {
			long beginPos = Long.parseLong(matcher.group(1));
			long endPos = -1;

			String endMatch = matcher.group(2);

			if (endMatch != null) {
				endPos = Long.parseLong(endMatch);
			}


			final long trackLength = trackFile.length();

			if (endPos < 0) {
				endPos = trackLength - 1;
			}


			if (beginPos < 0 || beginPos >= trackLength || endPos >= trackLength || endPos <= beginPos) {
				log.error("Bad \"Range\" values: {} - {}", beginPos, endPos);
				throw new WebApplicationException(416);
			}


			return new Range(beginPos, endPos);

		} catch (NumberFormatException e) {
			log.error("Bad \"Range\" header", e);
			throw new WebApplicationException(416);
		}

	}


	/**
	 * Simple pair of values with the constraint that start &lt; end
	 */
	private static class Range {
		Range(long start, long end) {
			Preconditions.checkArgument(start >= 0);
			Preconditions.checkArgument(end > 0);
			Preconditions.checkArgument(start < end);
			this.start = start;
			this.end = end;
		}

		long getStart() {
			return this.start;
		}

		long getEnd() {
			return this.end;
		}

		long getLength() {
			return end - start + 1;
		}

		private long start;
		private long end;
	}
}
