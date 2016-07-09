package com.perrier.music.rest.resource;

import java.util.Collection;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.inject.Inject;
import com.perrier.music.db.DBException;
import com.perrier.music.dto.track.TrackDto;
import com.perrier.music.dto.track.TrackDtoMapper;
import com.perrier.music.entity.track.Track;
import com.perrier.music.entity.track.TrackProvider;

@Path("api/shuffle")
@Produces(MediaType.APPLICATION_JSON)
public class ShuffleResource {

	@Inject
	private TrackProvider trackProvider;

	@GET
	public Collection<TrackDto> getAll() throws DBException {
		List<Track> tracks = this.trackProvider.findRandom();
		return TrackDtoMapper.build(tracks);
	}
}
