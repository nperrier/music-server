package com.perrier.music.rest.resource;

import java.util.Collection;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.inject.Inject;
import com.perrier.music.api.GenreDto;
import com.perrier.music.api.TrackDto;
import com.perrier.music.db.DBException;
import com.perrier.music.entity.genre.Genre;
import com.perrier.music.entity.genre.GenreProvider;
import com.perrier.music.entity.track.Track;
import com.perrier.music.entity.track.TrackProvider;
import com.perrier.music.rest.GenreDtoMapper;
import com.perrier.music.rest.TrackDtoMapper;

@Path("api/genre")
@Produces(MediaType.APPLICATION_JSON)
public class GenreResource {

	@Inject
	private GenreProvider genreProvider;

	@Inject
	private TrackProvider trackProvider;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<GenreDto> getAll() throws DBException {
		List<Genre> genres = this.genreProvider.findAll();
		return GenreDtoMapper.build(genres);
	}

	@GET
	@Path("{id}/tracks")
	@Produces(MediaType.APPLICATION_JSON)
	public List<TrackDto> getTracks(@PathParam("id") Long id) throws DBException {
		List<Track> tracks = this.trackProvider.findAllByGenreId(id);
		return TrackDtoMapper.build(tracks);
	}

}
