package com.perrier.music.rest.resource;

import java.util.Collection;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.inject.Inject;
import com.perrier.music.api.PlaylistDto;
import com.perrier.music.api.PlaylistTrackDto;
import com.perrier.music.db.DBException;
import com.perrier.music.entity.playlist.Playlist;
import com.perrier.music.entity.playlist.PlaylistProvider;
import com.perrier.music.rest.PlaylistDtoMapper;
import com.perrier.music.rest.PlaylistTrackDtoMapper;
import com.perrier.music.server.EntityNotFoundException;

@Path("api/playlist")
@Produces(MediaType.APPLICATION_JSON)
public class PlaylistResource {

	@Inject
	private
	PlaylistProvider playlistProvider;

	@GET
	public Collection<PlaylistDto> getAll() throws DBException {
		List<Playlist> playlists = this.playlistProvider.findAll(false /* no tracks */);
		return PlaylistDtoMapper.build(playlists);
	}

	@GET
	@Path("{id}")
	public PlaylistDto get(@PathParam("id") Long id) throws DBException {
		Playlist playlist = getPlaylist(id, false);
		PlaylistDto output = PlaylistDtoMapper.build(playlist);
		return output;
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createPlaylist(Playlist playlist) throws DBException {
		this.playlistProvider.create(playlist);
		PlaylistDto playlistDto = PlaylistDtoMapper.build(playlist);
		return Response.status(Response.Status.CREATED).entity(playlistDto).build();
	}

	@PUT
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@PathParam("id") Long id, PlaylistDto playlistUpdateDto) throws DBException {
		Playlist playlist = getPlaylist(id, false);

		Playlist updatedPlaylist = this.playlistProvider.update(playlist, playlistUpdateDto);

		return Response.status(Response.Status.CREATED).entity(updatedPlaylist).build();
	}

	@DELETE
	@Path("{id}")
	public Response deletePlaylist(@PathParam("id") Long id) throws DBException {
		Playlist playlist = getPlaylist(id, false);
		this.playlistProvider.delete(playlist);
		PlaylistDto playlistDto = PlaylistDtoMapper.build(playlist);
		return Response.status(Response.Status.NO_CONTENT).entity(playlistDto).build();
	}

	@GET
	@Path("{id}/tracks")
	public Collection<PlaylistTrackDto> getTracks(@PathParam("id") Long id) throws DBException {
		Playlist playlist = getPlaylist(id, true);
		Collection<PlaylistTrackDto> output = PlaylistTrackDtoMapper.build(playlist.getPlaylistTracks());
		return output;
	}

	@POST
	@Path("{id}/tracks")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addTracks(@PathParam("id") Long id, @QueryParam("position") Integer pos, List<Long> trackIds)
			throws DBException {
		Playlist playlist = getPlaylist(id, true);
		this.playlistProvider.addTracksToPlaylist(playlist, trackIds, pos);
		return Response.status(Response.Status.CREATED).build();
	}

	@PUT
	@Path("{id}/tracks")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateTracks(@PathParam("id") Long id, List<PlaylistTrackDto> tracks) throws DBException {
		Playlist playlist = getPlaylist(id, true);
		this.playlistProvider.updatePlaylistTracks(playlist, tracks);
		return Response.status(Response.Status.CREATED).build();
	}

	@DELETE
	@Path("{id}/tracks/{playlistTrackId}")
	public Response deleteTracks(@PathParam("id") Long id, @PathParam("playlistTrackId") Long playlistTrackId)
			throws DBException {
		Playlist playlist = getPlaylist(id, true);
		this.playlistProvider.removeTrack(playlist, playlistTrackId);
		return Response.status(Response.Status.OK).build();
	}

	@POST
	@Path("{id}/album/{albumId}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addAlbum(@PathParam("id") Long id, @PathParam("albumId") Long albumId,
			@QueryParam("position") Integer position) throws DBException {
		Playlist playlist = getPlaylist(id, true);
		this.playlistProvider.addAlbumToPlaylist(playlist, albumId, position);
		return Response.status(Response.Status.CREATED).build();
	}

	private Playlist getPlaylist(Long id, boolean includeTracks) throws DBException {
		Playlist playlist = this.playlistProvider.findById(id, includeTracks);
		if (playlist == null) {
			throw new EntityNotFoundException("Playlist not found");
		}
		return playlist;
	}
}
