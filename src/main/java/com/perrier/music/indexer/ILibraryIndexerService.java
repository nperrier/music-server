package com.perrier.music.indexer;

import java.util.concurrent.Future;

import com.google.common.util.concurrent.Service;

public interface ILibraryIndexerService extends Service {

	Future<Boolean> submit(LibraryIndexerTask task);

}
