package com.perrier.music.rest.resource

import java.util.List

import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.GenericEntity
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

import com.google.inject.Inject
import com.perrier.music.dto.album.AlbumDto
import com.perrier.music.dto.album.AlbumDtoMapper
import com.perrier.music.dto.track.TrackDto
import com.perrier.music.dto.track.TrackDtoMapper
import com.perrier.music.entity.album.Album
import com.perrier.music.entity.album.AlbumProvider
import com.perrier.music.entity.artist.Artist
import com.perrier.music.entity.artist.ArtistProvider
import com.perrier.music.entity.track.Track
import com.perrier.music.entity.track.TrackProvider
import com.perrier.music.server.EntityNotFoundException

@Path("api/album")
@Produces(MediaType.APPLICATION_JSON)
class AlbumResource extends RestResource {

	@Inject
	AlbumProvider albumProvider
	@Inject
	TrackProvider trackProvider

	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	def AlbumDto get(@PathParam("id") Long id) {
		
		Album album = this.albumProvider.findById(id)

		if (album == null) {
			throw new EntityNotFoundException("Album not found, id: " + id)
		}

		return AlbumDtoMapper.build(album)
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	def Collection<AlbumDto> getAll() {
		
		List<Album> albums = this.albumProvider.findAll()

		return AlbumDtoMapper.build(albums)
	}
	
	@GET
	@Path("{id}/tracks")
	@Produces(MediaType.APPLICATION_JSON)
	def List<TrackDto> getTracks(@PathParam("id") Long id) {

		List<Track> tracks = this.trackProvider.findAllByAlbumId(id)

		return TrackDtoMapper.build(tracks)
	}
}