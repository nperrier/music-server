package com.perrier.music.entity.track;

import com.perrier.music.entity.artist.Artist;
import com.perrier.music.entity.track.AbstractTrackUpdater.UpdateResult;

public interface ITrackAlbumUpdaterFactory {

	TrackAlbumUpdater create(Track track, UpdateResult<Artist> artist);

}
