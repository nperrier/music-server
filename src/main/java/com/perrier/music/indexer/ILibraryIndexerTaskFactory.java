package com.perrier.music.indexer;

import com.perrier.music.entity.library.Library;

public interface ILibraryIndexerTaskFactory {

	LibraryIndexerTask create(Library library);
	
}
