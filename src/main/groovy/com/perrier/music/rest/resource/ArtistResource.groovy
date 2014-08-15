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
import com.perrier.music.dto.artist.ArtistDto
import com.perrier.music.dto.artist.ArtistDtoMapper
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

@Path("api/artist")
@Produces(MediaType.APPLICATION_JSON)
class ArtistResource extends RestResource {

	@Inject
	ArtistProvider artistProvider
	@Inject
	AlbumProvider albumProvider
	@Inject
	TrackProvider trackProvider

	@GET
	@Path("{id}")
	def ArtistDto get(@PathParam("id") Long id) {

		Artist artist = this.artistProvider.findById(id)

		if (artist == null) {
			throw new EntityNotFoundException("Artist not found, id: " + id)
		}

		return ArtistDtoMapper.build(artist)
	}

	@GET
	def Collection<ArtistDto> getAll() {

		List<Artist> artists = this.artistProvider.findAll()

		return ArtistDtoMapper.build(artists)
	}

	@GET
	@Path("{id}/tracks")
	def List<TrackDto> getTracks(@PathParam("id") Long id) {

		List<Track> tracks = this.trackProvider.findAllByArtistId(id)

		return TrackDtoMapper.build(tracks)
	}

	@GET
	@Path("{id}/albums")
	def List<AlbumDto> getAlbums(@PathParam("id") Long id) {

		List<Album> albums = this.albumProvider.findAllByArtistId(id)

		return AlbumDtoMapper.build(albums)
	}
}