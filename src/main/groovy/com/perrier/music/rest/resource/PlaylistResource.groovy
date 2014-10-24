package com.perrier.music.rest.resource

import javax.ws.rs.Consumes
import javax.ws.rs.DELETE
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

import com.google.inject.Inject
import com.perrier.music.dto.playlist.PlaylistDto
import com.perrier.music.dto.playlist.PlaylistDtoMapper
import com.perrier.music.dto.playlist.PlaylistTrackDto
import com.perrier.music.dto.playlist.PlaylistTrackDtoMapper
import com.perrier.music.entity.playlist.Playlist
import com.perrier.music.entity.playlist.PlaylistProvider
import com.perrier.music.server.EntityNotFoundException

@Path("api/playlist")
@Produces(MediaType.APPLICATION_JSON)
class PlaylistResource extends RestResource {

	@Inject
	PlaylistProvider playlistProvider

	@GET
	def Collection<PlaylistDto> getAll() {

		List<Playlist> playlists = this.playlistProvider.findAll(false /* no tracks */)

		return PlaylistDtoMapper.build(playlists)
	}

	@GET
	@Path("{id}")
	public PlaylistDto get(@PathParam("id") Long id) {

		Playlist playlist = this.playlistProvider.findById(id, false /* no tracks */)

		if (!playlist) {
			throw new EntityNotFoundException("Playlist not found, id: " + id)
		}

		def output = PlaylistDtoMapper.build(playlist)
		return output
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createPlaylist(Playlist playlist) {

		this.playlistProvider.create(playlist)

		def playlistDto = PlaylistDtoMapper.build(playlist)
		return Response.status(Response.Status.CREATED).entity(playlistDto).build()
	}

	@DELETE
	@Path("{id}")
	public Response deletePlaylist(@PathParam("id") Long id) {

		Playlist playlist = this.playlistProvider.findById(id, false /* no tracks */)

		if (!playlist) {
			throw new EntityNotFoundException("Playlist not found, id: " + id)
		}

		this.playlistProvider.delete(playlist)

		def playlistDto = PlaylistDtoMapper.build(playlist)
		return Response.status(Response.Status.NO_CONTENT).entity(playlistDto).build()
	}

	@GET
	@Path("{id}/tracks")
	public Collection<PlaylistTrackDto> getTracks(@PathParam("id") Long id) {

		Playlist playlist = this.playlistProvider.findById(id, true /* include tracks */)

		if (!playlist) {
			throw new EntityNotFoundException("Playlist not found, id: " + id)
		}

		def output = PlaylistTrackDtoMapper.build(playlist.getPlaylistTracks())
		return output
	}

	@POST
	@Path("{id}/tracks")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addTracks(
			@PathParam("id") Long id, @QueryParam("position") Integer position, List<Long> playlistTrackIds) {
		// TODO: Wrap all this logic in a separate class that throws exceptions that the API translates to Response codes
		// TODO validate the path before creating
		Playlist playlist = this.playlistProvider.findById(id, true)

		if (!playlist) {
			throw new EntityNotFoundException("Playlist not found, id: " + id)
		}

		this.playlistProvider.addTracksToPlaylist(playlist, playlistTrackIds, position)

		return Response.status(Response.Status.CREATED).build()
	}

}
