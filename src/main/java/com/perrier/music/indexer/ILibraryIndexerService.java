package com.perrier.music.indexer;

import com.google.common.util.concurrent.Service;

public interface ILibraryIndexerService extends Service {

	void submit(LibraryIndexerTask task);

}
