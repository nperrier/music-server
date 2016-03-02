package com.perrier.music.rest.resource

import com.google.inject.Inject
import com.perrier.music.dto.playlist.PlaylistDto
import com.perrier.music.dto.playlist.PlaylistDtoMapper
import com.perrier.music.dto.playlist.PlaylistTrackDto
import com.perrier.music.dto.playlist.PlaylistTrackDtoMapper
import com.perrier.music.entity.playlist.Playlist
import com.perrier.music.entity.playlist.PlaylistProvider
import com.perrier.music.server.EntityNotFoundException

import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

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
		Playlist playlist = getPlaylist(id)
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
		Playlist playlist = getPlaylist(id)
		this.playlistProvider.delete(playlist)
		def playlistDto = PlaylistDtoMapper.build(playlist)
		return Response.status(Response.Status.NO_CONTENT).entity(playlistDto).build()
	}

	@GET
	@Path("{id}/tracks")
	public Collection<PlaylistTrackDto> getTracks(@PathParam("id") Long id) {
		Playlist playlist = getPlaylist(id, true)
		def output = PlaylistTrackDtoMapper.build(playlist.getPlaylistTracks())
		return output
	}

	@POST
	@Path("{id}/tracks")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addTracks(@PathParam("id") Long id, @QueryParam("position") Integer pos, List<Long> trackIds) {
		Playlist playlist = getPlaylist(id, true)
		this.playlistProvider.addTracksToPlaylist(playlist, trackIds, pos)
		return Response.status(Response.Status.CREATED).build()
	}

	@PUT
	@Path("{id}/tracks")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateTracks(@PathParam("id") Long id, List<PlaylistTrackDto> tracks) {
		Playlist playlist = getPlaylist(id, true)
		this.playlistProvider.updatePlaylistTracks(playlist, tracks)
		return Response.status(Response.Status.CREATED).build()
	}


	@DELETE
	@Path("{id}/tracks/{playlistTrackId}")
	public Response deleteTracks(@PathParam("id") Long id, @PathParam("playlistTrackId") Long playlistTrackId) {
		Playlist playlist = getPlaylist(id, true)
		this.playlistProvider.removeTrack(playlist, playlistTrackId)
		return Response.status(Response.Status.OK).build()
	}

	@POST
	@Path("{id}/album/{albumId}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addAlbum(
			@PathParam("id") Long id,
			@PathParam("albumId") Long albumId,
			@QueryParam("position") Integer position) {
		Playlist playlist = getPlaylist(id, true)
		this.playlistProvider.addAlbumToPlaylist(playlist, albumId, position)
		return Response.status(Response.Status.CREATED).build()
	}

	def getPlaylist(Long id, boolean includeTracks = false) {
		Playlist playlist = this.playlistProvider.findById(id, includeTracks)
		if (!playlist) {
			throw new EntityNotFoundException("Playlist not found")
		}
		return playlist
	}
}
