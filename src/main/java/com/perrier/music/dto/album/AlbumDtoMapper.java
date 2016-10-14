package com.perrier.music.dto.album;

import java.util.List;

import com.google.common.collect.Lists;
import com.perrier.music.dto.artist.ArtistDtoMapper;
import com.perrier.music.entity.album.Album;

public class AlbumDtoMapper {

	private static final AlbumDto EMPTY_ALBUM = new AlbumDto();

	private AlbumDtoMapper() {
	}

	public static AlbumDto build(Album album) {

		if (album == null) {
			return EMPTY_ALBUM;
		}

		AlbumDto dto = new AlbumDto();
		dto.setId(album.getId());
		dto.setName(album.getName());
		dto.setYear(album.getYear());
		dto.setArtist(ArtistDtoMapper.build(album.getArtist()));
		dto.setCoverArtUrl("/api/cover/album/" + album.getId());
		dto.setDownloadUrl("/api/album/download/" + album.getId());
		dto.setCreationDate(album.getCreationDate());
		dto.setModificationDate(album.getModificationDate());

		return dto;
	}

	public static List<AlbumDto> build(List<Album> albums) {

		List<AlbumDto> dtos = Lists.newArrayListWithCapacity(albums.size());
		for (Album album : albums) {
			dtos.add(build(album));
		}

		return dtos;
	}
}
