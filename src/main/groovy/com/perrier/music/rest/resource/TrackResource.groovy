package com.perrier.music.rest.resource

import com.google.inject.Inject
import com.perrier.music.dto.track.TrackDto
import com.perrier.music.dto.track.TrackDtoMapper
import com.perrier.music.dto.track.TrackUpdateDto
import com.perrier.music.entity.track.Track
import com.perrier.music.entity.track.TrackProvider
import com.perrier.music.rest.stream.FileStreamer
import com.perrier.music.server.EntityExistsException
import com.perrier.music.server.EntityNotFoundException

import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

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

	@PUT
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateTrack(@PathParam("id") Long id, TrackUpdateDto trackUpdateDto) {

		Track track = this.trackProvider.findById(id)
		if (!track) {
			throw new EntityExistsException("Track does not exist")
		}

		Track updatedTrack = this.trackProvider.update(track, trackUpdateDto)

		return Response.status(Response.Status.CREATED).entity(updatedTrack).build()
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

		File trackFile = new File(track.getPath())
		def filename = trackFile.name
		FileStreamer stream = new FileStreamer(trackFile)

		return Response.ok(stream)
				.type("audio/mpeg")
				.header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
				.build()
	}
}