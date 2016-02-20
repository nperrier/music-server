package com.perrier.music.entity.track;

import com.google.inject.assistedinject.Assisted;

import com.perrier.music.entity.artist.Artist;
import com.perrier.music.entity.track.AbstractTrackUpdater.UpdateResult;

public interface ITrackAlbumUpdaterFactory {

	TrackAlbumUpdater create(Track track, @Assisted("albumArtist") UpdateResult<Artist> albumArtist, @Assisted("artist") UpdateResult<Artist> artist);

}
