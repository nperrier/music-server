package com.perrier.music.rest.resource;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.inject.Inject;
import com.perrier.music.api.TrackDto;
import com.perrier.music.api.TrackMetaData;
import com.perrier.music.api.TrackUpdateDto;
import com.perrier.music.db.DBException;
import com.perrier.music.entity.track.Track;
import com.perrier.music.entity.track.TrackProvider;
import com.perrier.music.indexer.LibraryService;
import com.perrier.music.rest.TrackDtoMapper;
import com.perrier.music.rest.stream.HttpStreamer;
import com.perrier.music.server.EntityNotFoundException;
import com.perrier.music.storage.S3StorageService;

@Path("api/track")
@Produces(MediaType.APPLICATION_JSON)
public class TrackResource {

	@Inject
	private TrackProvider trackProvider;

	@Inject
	private LibraryService libraryService;

	@Inject
	private S3StorageService storageService;

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

		InputStream audioStream = storageService.getAudioStream(track.getAudioStorageKey());
		HttpStreamer stream = new HttpStreamer(audioStream);
		String filename = createFileName(track);

		return Response.ok(stream) //
				.type("audio/mpeg") //
				.header("Content-Disposition", "attachment; filename=\"" + filename + "\"") //
				.build();
	}

	private static String createFileName(Track track) {
		StringBuilder b = new StringBuilder();

		if (track.getNumber() != null) {
			b.append(track.getNumber()).append("-");
		}

		b.append(track.getName());

		if (track.getArtist() != null) {
			b.append("-").append(track.getArtist().getName());
		}

		b.append(".mp3");

		return b.toString();
	}

	@POST
	@Consumes("application/json")
	@Produces("application/json")
	public Response createTrack(TrackMetaData metaData) {

		Track track = this.libraryService.addTrack(metaData);
		metaData.setId(track.getId());

		return Response.status(Response.Status.CREATED).entity(metaData).build();
	}

	@PUT
	@Consumes("application/json")
	@Produces("application/json")
	public Response updateTrack(TrackMetaData metaData) {

		this.libraryService.updateTrack(metaData);

		return Response.ok().build();
	}

	private Track getTrack(Long id) throws DBException {
		Track track = this.trackProvider.findById(id);
		if (track == null) {
			throw new EntityNotFoundException("Track not found");
		}
		return track;
	}
}
