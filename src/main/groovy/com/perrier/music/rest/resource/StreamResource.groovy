
package com.perrier.music.rest.resource

import java.io.IOException

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.WebApplicationException
import javax.ws.rs.core.Response
import javax.ws.rs.core.StreamingOutput

import com.google.inject.Inject
import com.perrier.music.entity.track.Track
import com.perrier.music.entity.track.TrackProvider
import com.perrier.music.server.EntityNotFoundException
import com.perrier.music.stream.StreamException
import com.perrier.music.stream.TrackStreamer

@Path("api/stream")
public class StreamResource extends RestResource {

	@Inject
	private TrackProvider trackProvider

	@GET
	@Path("{id}")
	@Produces([
		"audio/mpeg",
		"application/json"
	])
	//@Produces("audio/mpeg")
	public Response get(@PathParam("id") Long id) {

		Track track = this.trackProvider.findById(id)

		if (track == null) {
			throw new EntityNotFoundException("track not found")
		}

		StreamingOutput stream = new StreamingOutput() {
					@Override
					public void write(OutputStream os) throws IOException, WebApplicationException {
						try {
							TrackStreamer streamer = new TrackStreamer(track)
							streamer.writeStream(os)
						}
						catch (Exception e) {
							throw new WebApplicationException(e)
						}
					}
				}

		return Response.ok(stream).type("audio/mpeg").build()
	}
}
