package com.perrier.music.dto.search;

import java.util.List;

import com.perrier.music.dto.album.AlbumDto;
import com.perrier.music.dto.album.AlbumDtoMapper;
import com.perrier.music.dto.artist.ArtistDto;
import com.perrier.music.dto.artist.ArtistDtoMapper;
import com.perrier.music.dto.track.TrackDto;
import com.perrier.music.dto.track.TrackDtoMapper;
import com.perrier.music.search.SearchResults;

public class SearchResultsDtoMapper {

	private SearchResultsDtoMapper() {
	}

	public static SearchResultsDto build(SearchResults searchResults) {
		SearchResultsDto dto = new SearchResultsDto();

		List<AlbumDto> albumDtos = AlbumDtoMapper.build(searchResults.getAlbums());
		dto.setAlbums(albumDtos);
		dto.setAlbumsTotal(searchResults.getAlbumsTotal());

		List<ArtistDto> artistDtos = ArtistDtoMapper.build(searchResults.getArtists());
		dto.setArtists(artistDtos);
		dto.setArtistsTotal(searchResults.getArtistsTotal());

		List<TrackDto> trackDtos = TrackDtoMapper.build(searchResults.getTracks());
		dto.setTracks(trackDtos);
		dto.setTracksTotal(searchResults.getTracksTotal());

		return dto;
	}
}
