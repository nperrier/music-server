package com.perrier.music.rest.resource;

import java.io.File;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.inject.Inject;
import com.perrier.music.db.DBException;
import com.perrier.music.dto.track.TrackDto;
import com.perrier.music.dto.track.TrackDtoMapper;
import com.perrier.music.dto.track.TrackUpdateDto;
import com.perrier.music.entity.track.Track;
import com.perrier.music.entity.track.TrackProvider;
import com.perrier.music.rest.stream.FileStreamer;
import com.perrier.music.server.EntityNotFoundException;

@Path("api/track")
@Produces(MediaType.APPLICATION_JSON)
public class TrackResource {

	@Inject
	private TrackProvider trackProvider;

	//	@Inject
	//	private ILibraryService libraryService;

	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public TrackDto get(@PathParam("id") Long id) throws DBException {

		Track track = getTrack(id);
		TrackDto trackDto = TrackDtoMapper.build(track);

		return trackDto;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<TrackDto> getAll() throws DBException {
		List<Track> tracks = this.trackProvider.findAll();
		return TrackDtoMapper.build(tracks);
	}

	@PUT
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateTrack(@PathParam("id") Long id, TrackUpdateDto trackUpdateDto) throws DBException {

		Track track = getTrack(id);
		Track updatedTrack = this.trackProvider.update(track, trackUpdateDto);

		return Response.status(Response.Status.CREATED).entity(updatedTrack).build();
	}

	@GET
	@Path("download/{id}")
	@Produces({ "audio/mpeg", "application/json" })
	public Response download(@PathParam("id") Long id) throws DBException {

		Track track = getTrack(id);

		File trackFile = new File(track.getPath());
		String filename = trackFile.getName();
		FileStreamer stream = new FileStreamer(trackFile);

		return Response.ok(stream) //
				.type("audio/mpeg") //
				.header("Content-Disposition", "attachment; filename=\"" + filename + "\"") //
				.build();
	}

	private Track getTrack(Long id) throws DBException {
		Track track = this.trackProvider.findById(id);
		if (track == null) {
			throw new EntityNotFoundException("Track not found");
		}
		return track;
	}

	//	@GET
	//	@Path("scan/{id}")
	//	@Produces({ "application/json" })
	//	public Response scan(@PathParam("id") Long id) throws DBException {
	//
	//		Track track = this.trackProvider.findById(id);
	//
	//		if (track == null) {
	//			throw new EntityNotFoundException("track not found");
	//		}
	//
	//		File trackFile = new File(track.getPath());
	//
	//		ChangedTrackEvent evt = new ChangedTrackEvent(trackFile, track, track.getLibrary());
	//		libraryService.handle(evt);
	//
	//		return Response.ok() //
	//				.build();
	//	}
}
