package com.perrier.music.rest.resource

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

import com.google.inject.Inject
import com.perrier.music.dto.genre.GenreDto
import com.perrier.music.dto.genre.GenreDtoMapper
import com.perrier.music.entity.genre.Genre
import com.perrier.music.entity.genre.GenreProvider

@Path("api/genre")
@Produces(MediaType.APPLICATION_JSON)
class GenreResource extends RestResource {

	@Inject
	GenreProvider genreProvider

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	def Collection<GenreDto> getAll() {

		List<Genre> genres = this.genreProvider.findAll()

		return GenreDtoMapper.build(genres)
	}
}
