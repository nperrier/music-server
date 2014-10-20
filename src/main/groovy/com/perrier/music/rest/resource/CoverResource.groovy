
package com.perrier.music.rest.resource

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.WebApplicationException
import javax.ws.rs.core.Response
import javax.ws.rs.core.StreamingOutput

import com.google.inject.Inject
import com.perrier.music.coverart.ICoverArtService
import com.perrier.music.coverart.CoverArtService.Type
import com.perrier.music.stream.CoverArtStreamer
import com.perrier.music.stream.StreamException

@Path("api/cover")
class CoverResource extends RestResource {

	@Inject
	private ICoverArtService coverArtService

	@GET
	@Path("artist/{id}")
	def Response getByArtist(@PathParam("id") Long id) {
		return get(Type.ARTIST, id)
	}

	@GET
	@Path("album/{id}")
	def Response getByAlbum(@PathParam("id") Long id) {
		return get(Type.ALBUM, id)
	}

	@GET
	@Path("track/{id}")
	def Response getByTrack(@PathParam("id") Long id) {
		return get(Type.TRACK, id)
	}

	def Response get(Type type, Long id) {

		File coverFile = coverArtService.getCoverFile(type, id)
		def mimeType = getMimeType(coverFile)

		StreamingOutput stream = new StreamingOutput() {
					@Override
					public void write(OutputStream os) throws IOException, WebApplicationException {
						try {
							CoverArtStreamer streamer = new CoverArtStreamer(coverFile)
							streamer.writeStream(os)
						}
						catch (StreamException e) {
							throw new WebApplicationException(e)
						}
					}
				}

		return Response.ok(stream).type(mimeType).build()
	}
}
