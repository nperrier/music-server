package com.perrier.music.rest.resource;

import java.io.File;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import com.google.inject.Inject;

import com.perrier.music.coverart.CoverArtException;
import com.perrier.music.coverart.CoverArtService.Type;
import com.perrier.music.coverart.ICoverArtService;
import com.perrier.music.rest.stream.FileStreamer;

@Path("api/cover")
public class CoverResource {

	@Inject
	private ICoverArtService coverArtService;

	@GET
	@Path("artist/{id}")
	public Response getByArtist(@PathParam("id") Long id) {
		return get(Type.ARTIST, id);
	}

	@GET
	@Path("album/{id}")
	public Response getByAlbum(@PathParam("id") Long id) {
		return get(Type.ALBUM, id);
	}

	@GET
	@Path("track/{id}")
	public Response getByTrack(@PathParam("id") Long id) {
		return get(Type.TRACK, id);
	}

	private Response get(Type type, Long id) {

		File coverFile = null;
		try {
			coverFile = coverArtService.getCoverFile(type, id);
			String mimeType = "image/png"; // TODO
			FileStreamer stream = new FileStreamer(coverFile);

			return Response.ok(stream).type(mimeType).build();

		} catch (CoverArtException e) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}
}
