package com.perrier.music.rest.resource

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

import com.google.inject.Inject
import com.perrier.music.dto.genre.GenreDto
import com.perrier.music.dto.genre.GenreDtoMapper
import com.perrier.music.dto.track.TrackDto
import com.perrier.music.dto.track.TrackDtoMapper
import com.perrier.music.entity.genre.Genre
import com.perrier.music.entity.genre.GenreProvider
import com.perrier.music.entity.track.Track
import com.perrier.music.entity.track.TrackProvider

@Path("api/genre")
@Produces(MediaType.APPLICATION_JSON)
class GenreResource extends RestResource {

	@Inject
	GenreProvider genreProvider

	@Inject
	TrackProvider trackProvider

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	def Collection<GenreDto> getAll() {

		List<Genre> genres = this.genreProvider.findAll()

		return GenreDtoMapper.build(genres)
	}

	@GET
	@Path("{id}/tracks")
	@Produces(MediaType.APPLICATION_JSON)
	def List<TrackDto> getTracks(@PathParam("id") Long id) {

		List<Track> tracks = this.trackProvider.findAllByGenreId(id)

		return TrackDtoMapper.build(tracks)
	}

}
