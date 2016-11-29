package com.perrier.music.coverart;

import java.io.File;

import com.perrier.music.coverart.CoverArtService.Type;

public interface ICoverArtService {

	File getCoverFile(Type type, Long id) throws CoverArtException;

}
