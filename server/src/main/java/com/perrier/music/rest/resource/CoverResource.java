package com.perrier.music.rest.resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.activation.MimetypesFileTypeMap;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;

import com.google.inject.Inject;
import com.perrier.music.coverart.CoverArtException;
import com.perrier.music.coverart.CoverArtService.Type;
import com.perrier.music.coverart.ICoverArtService;
import com.perrier.music.rest.stream.FileStreamer;
import com.perrier.music.server.auth.NoAuthentication;
import com.perrier.music.storage.S3StorageService;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataParam;

@Path("api/cover")
@Produces({ "application/svg+xml", "image/png" })
public class CoverResource {

	@Inject
	private ICoverArtService coverArtService;

	@Inject
	private S3StorageService storageService;

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

	@PUT
	@Path("track/{id}")
	@Produces({ MediaType.APPLICATION_JSON })
	@Consumes({ MediaType.MULTIPART_FORM_DATA })
	public Response update(
			@FormDataParam("file") InputStream fileInputStream,
			@FormDataParam("file") FormDataContentDisposition fileMetaData,
			@FormDataParam("file") FormDataBodyPart bodyPart) {

		byte[] imageData;
		try {
			imageData = IOUtils.toByteArray(fileInputStream);
		} catch (IOException ioe) {
			throw new WebApplicationException(ioe);
		}

		String coverHash;
		try {
			coverHash = createHash(imageData);
		} catch (NoSuchAlgorithmException nsae) {
			throw new WebApplicationException(nsae);
		}

		String contentType = bodyPart.getMediaType().toString();

		// TODO: validate content type, otherwise return 400:
		URL url = storageService.putCover(coverHash, imageData, contentType);

		String coverKey = S3StorageService.getCoverKey(coverHash, contentType);
		PutResponse response = new PutResponse(coverHash, coverKey, url.toExternalForm());

		return Response.ok(response).build();
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

	private static String createHash(byte[] bytes) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA1");
		byte[] digest = md.digest(bytes);
		String hash = DigestUtils.sha1Hex(digest);
		return hash;
	}

	private static class PutResponse {

		private String coverHash;
		private String coverKey;
		private String coverUrl;

		public PutResponse(String coverHash, String coverKey, String coverUrl) {
			this.coverHash = coverHash;
			this.coverKey = coverKey;
			this.coverUrl = coverUrl;
		}

		public String getCoverUrl() {
			return coverUrl;
		}

		public String getCoverKey() {
			return coverKey;
		}

		public String getCoverHash() {
			return coverHash;
		}
	}
}
