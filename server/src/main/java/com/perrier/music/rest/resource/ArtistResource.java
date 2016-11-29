package com.perrier.music.rest.resource;

import java.util.Collection;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.inject.Inject;
import com.perrier.music.api.AlbumDto;
import com.perrier.music.api.ArtistDto;
import com.perrier.music.api.TrackDto;
import com.perrier.music.db.DBException;
import com.perrier.music.entity.album.Album;
import com.perrier.music.entity.album.AlbumProvider;
import com.perrier.music.entity.artist.Artist;
import com.perrier.music.entity.artist.ArtistProvider;
import com.perrier.music.entity.track.Track;
import com.perrier.music.entity.track.TrackProvider;
import com.perrier.music.rest.AlbumDtoMapper;
import com.perrier.music.rest.ArtistDtoMapper;
import com.perrier.music.rest.TrackDtoMapper;
import com.perrier.music.server.EntityNotFoundException;

@Path("api/artist")
@Produces(MediaType.APPLICATION_JSON)
public class ArtistResource {

	@Inject
	private ArtistProvider artistProvider;

	@Inject
	private AlbumProvider albumProvider;

	@Inject
	private TrackProvider trackProvider;

	@GET
	@Path("{id}")
	public ArtistDto get(@PathParam("id") Long id) throws DBException {

		Artist artist = this.artistProvider.findById(id);

		if (artist == null) {
			throw new EntityNotFoundException("Artist not found, id: " + id);
		}

		return ArtistDtoMapper.build(artist);
	}

	@GET
	public Collection<ArtistDto> getAll() throws DBException {
		List<Artist> artists = this.artistProvider.findAll();
		return ArtistDtoMapper.build(artists);
	}

	@GET
	@Path("{id}/tracks")
	public List<TrackDto> getTracks(@PathParam("id") Long id) throws DBException {
		List<Track> tracks = this.trackProvider.findAllByArtistId(id);
		return TrackDtoMapper.build(tracks);
	}

	@GET
	@Path("{id}/albums")
	public List<AlbumDto> getAlbums(@PathParam("id") Long id) throws DBException {
		List<Album> albums = this.albumProvider.findAllByArtistId(id);
		return AlbumDtoMapper.build(albums);
	}

}
