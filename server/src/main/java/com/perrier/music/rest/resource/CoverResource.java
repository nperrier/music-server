package com.perrier.music.rest.resource;

import java.io.File;

import javax.activation.MimetypesFileTypeMap;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.google.inject.Inject;
import com.perrier.music.coverart.CoverArtException;
import com.perrier.music.coverart.CoverArtService.Type;
import com.perrier.music.coverart.ICoverArtService;
import com.perrier.music.rest.stream.FileStreamer;
import com.perrier.music.server.auth.NoAuthentication;

@Path("api/cover")
@Produces({ "application/svg+xml", "image/png" })
public class CoverResource {

	@Inject
	private ICoverArtService coverArtService;

	@GET
	@Path("artist/{id}")
	@NoAuthentication
	public Response getByArtist(@PathParam("id") Long id) {
		return get(Type.ARTIST, id);
	}

	@GET
	@Path("album/{id}")
	@NoAuthentication
	public Response getByAlbum(@PathParam("id") Long id) {
		return get(Type.ALBUM, id);
	}

	@GET
	@Path("track/{id}")
	@NoAuthentication
	public Response getByTrack(@PathParam("id") Long id) {
		return get(Type.TRACK, id);
	}

	private Response get(Type type, Long id) {
		File coverFile = null;
		try {
			coverFile = coverArtService.getCoverFile(type, id);
			FileStreamer stream = new FileStreamer(coverFile);
			String mimeType = MimetypesFileTypeMap.getDefaultFileTypeMap().getContentType(coverFile);
			return Response.ok(stream).type(mimeType).build();

		} catch (CoverArtException e) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}
}
