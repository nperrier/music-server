package com.perrier.music.rest.resource;

import java.io.File;
import java.io.IOException;
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
import com.perrier.music.api.AlbumDto;
import com.perrier.music.api.AlbumUpdateDto;
import com.perrier.music.api.TrackDto;
import com.perrier.music.db.DBException;
import com.perrier.music.db.IDatabase;
import com.perrier.music.entity.album.Album;
import com.perrier.music.entity.album.AlbumProvider;
import com.perrier.music.entity.album.AlbumZipper;
import com.perrier.music.entity.track.Track;
import com.perrier.music.entity.track.TrackProvider;
import com.perrier.music.rest.AlbumDtoMapper;
import com.perrier.music.rest.TrackDtoMapper;
import com.perrier.music.rest.stream.FileStreamer;
import com.perrier.music.server.EntityExistsException;
import com.perrier.music.server.EntityNotFoundException;

@Path("api/album")
@Produces(MediaType.APPLICATION_JSON)
public class AlbumResource {

	@Inject
	private IDatabase db;

	@Inject
	private AlbumProvider albumProvider;

	@Inject
	private TrackProvider trackProvider;

	@Inject
	AlbumZipper albumZipper;

	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public AlbumDto get(@PathParam("id") Long id) throws DBException {

		Album album = this.albumProvider.findById(id);

		if (album == null) {
			throw new EntityNotFoundException("Album not found");
		}

		return AlbumDtoMapper.build(album);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<AlbumDto> getAll() throws DBException {

		List<Album> albums = this.albumProvider.findAll();

		return AlbumDtoMapper.build(albums);
	}

	@GET
	@Path("{id}/tracks")
	@Produces(MediaType.APPLICATION_JSON)
	public List<TrackDto> getTracks(@PathParam("id") Long id) throws DBException {

		List<Track> tracks = this.trackProvider.findAllByAlbumId(id);

		return TrackDtoMapper.build(tracks);
	}

	@GET
	@Path("download/{id}")
	@Produces("application/zip")
	public Response download(@PathParam("id") Long id) throws DBException, IOException {

		Album album = this.albumProvider.findByIdWithTracks(id);

		if (album == null) {
			throw new EntityNotFoundException("Album not found");
		}

		File zipFile = this.albumZipper.zip(album);
		FileStreamer stream = new FileStreamer(zipFile);
		String filename = album.getArtist().getName() + "-" + album.getName() + ".zip";

		return Response.ok(stream)
				.type("application/zip")
				.header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
				.build();
	}

	@PUT
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateAlbum(@PathParam("id") Long id, AlbumUpdateDto albumUpdateDto) throws DBException {
		try {
			this.db.beginTransaction();

			Album album = this.albumProvider.findById(id);
			if (album == null) {
				throw new EntityExistsException("Track does not exist");
			}

			Album updatedAlbum = this.albumProvider.update(album, albumUpdateDto);
			this.db.commit();

			return Response.status(Response.Status.CREATED).entity(updatedAlbum).build();

		} finally {
				this.db.endTransaction();
		}
	}

}