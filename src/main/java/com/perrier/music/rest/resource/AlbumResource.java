package com.perrier.music.rest.resource;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import com.google.inject.Inject;
import com.perrier.music.db.DBException;
import com.perrier.music.dto.album.AlbumDto;
import com.perrier.music.dto.album.AlbumDtoMapper;
import com.perrier.music.dto.track.TrackDto;
import com.perrier.music.dto.track.TrackDtoMapper;
import com.perrier.music.entity.album.Album;
import com.perrier.music.entity.album.AlbumProvider;
import com.perrier.music.entity.track.Track;
import com.perrier.music.entity.track.TrackProvider;
import com.perrier.music.server.EntityNotFoundException;

@Path("api/album")
@Produces(MediaType.APPLICATION_JSON)
public class AlbumResource {

	@Inject
	private AlbumProvider albumProvider;

	@Inject
	private TrackProvider trackProvider;

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
	public Response download(@PathParam("id") Long id) throws DBException {

		Album album = this.albumProvider.findById(id);

		if (album == null) {
			throw new EntityNotFoundException("Album not found");
		}

		// TODO: create zip of all tracks - name

		StreamingOutput stream = new StreamingOutput() {

			@Override
			public void write(OutputStream os) throws IOException, WebApplicationException {
				try {
					// TODO: Stream zipped album file
				} catch (Exception e) {
					throw new WebApplicationException(e);
				}
			}
		};

		String filename = album.getArtist().getName() + "-" + album.getName() + ".zip";

		return Response.ok(stream)
				.type("application/zip")
				.header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
				.build();
	}
}