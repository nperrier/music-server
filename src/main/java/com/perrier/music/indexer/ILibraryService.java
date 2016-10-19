package com.perrier.music.indexer;

import com.google.common.util.concurrent.Service;
import com.perrier.music.indexer.event.ChangedTrackEvent;
import com.perrier.music.indexer.event.MissingTrackEvent;
import com.perrier.music.indexer.event.UnknownTrackEvent;

public interface ILibraryService extends Service {

	void handle(MissingTrackEvent event);

	void handle(ChangedTrackEvent event);

	void handle(UnknownTrackEvent event);

}
