package com.perrier.music.rest.resource

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.WebApplicationException
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.core.StreamingOutput

import com.google.inject.Inject
import com.perrier.music.dto.track.TrackDto
import com.perrier.music.dto.track.TrackDtoMapper
import com.perrier.music.entity.track.Track
import com.perrier.music.entity.track.TrackProvider
import com.perrier.music.server.EntityNotFoundException
import com.perrier.music.stream.TrackStreamer

@Path("api/track")
@Produces(MediaType.APPLICATION_JSON)
public class TrackResource extends RestResource {

	@Inject
	private TrackProvider trackProvider

	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public TrackDto get(@PathParam("id") Long id) {

		Track track = this.trackProvider.findById(id)

		if (track == null) {
			throw new EntityNotFoundException()
		}

		TrackDto trackDto = TrackDtoMapper.build(track)

		return trackDto
	}

	// TODO: Paginate this by default - massive library will have TOO MANY tracks!
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	def Collection<TrackDto> getAll() {

		List<Track> tracks = this.trackProvider.findAll()

		return TrackDtoMapper.build(tracks)
	}

	@GET
	@Path("download/{id}")
	@Produces([
			"audio/mpeg",
			"application/json"
	])
	public Response download(@PathParam("id") Long id) {

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

		def filename = new File(track.getPath()).name

		return Response.ok(stream)
				.type("audio/mpeg")
				.header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
				.build()
	}
}