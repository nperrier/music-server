package com.perrier.music.rest;

import java.util.List;

import com.perrier.music.api.AlbumDto;
import com.perrier.music.api.ArtistDto;
import com.perrier.music.api.SearchResultsDto;
import com.perrier.music.api.TrackDto;
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
