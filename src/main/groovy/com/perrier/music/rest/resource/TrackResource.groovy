
package com.perrier.music.rest.resource

import java.util.Collection;

import javax.ws.rs.WebApplicationException
import javax.ws.rs.core.Response
import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

import com.google.inject.Inject

import com.perrier.music.entity.TrackProvider
import com.perrier.music.dto.album.AlbumDto;
import com.perrier.music.dto.track.TrackDto;
import com.perrier.music.dto.track.TrackDtoMapper
import com.perrier.music.entity.artist.Artist
import com.perrier.music.entity.track.Track
import com.perrier.music.entity.track.TrackProvider
import com.perrier.music.server.EntityNotFoundException

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
}