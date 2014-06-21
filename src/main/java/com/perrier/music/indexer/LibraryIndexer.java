package com.perrier.music.indexer;

import java.io.File;
import java.io.FileFilter;
import java.util.Date;

import com.google.common.base.Preconditions;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.perrier.music.db.DBException;
import com.perrier.music.db.IDatabase;
import com.perrier.music.entity.library.Library;
import com.perrier.music.entity.library.LibraryUpdateQuery;
import com.perrier.music.indexer.event.UnknownTrackEvent;

public class LibraryIndexer implements ILibraryIndexer {

	private FileFilter filter;
	private EventBus eventBus;
	private IDatabase db;

	private final Library library; // root dir of a libary

	@Inject
	public void setMusicFileFilter(FileFilter filter) {
		this.filter = filter;
	}

	@Inject
	public void setEventBus(EventBus eventBus) {
		this.eventBus = eventBus;
	}

	@Inject
	public void setDatabase(IDatabase db) {
		this.db = db;
	}
	
	public LibraryIndexer(Library library) {
		Preconditions.checkNotNull(library);
		this.library = library;
	}

	public void scan() throws LibraryIndexerException {
		
		// TODO
		// cache existing files (from db)
		// check existing files for CHANGES 
		// check for NEW files
		// check for MISSING files
		
		scan(new File(this.library.getPath()));
	}

	private void scan(File directory) throws LibraryIndexerException {
		
		if (!isValidDirectory(directory)) {
			return;
		}

		for (File file : directory.listFiles(filter)) {
			if (file.isDirectory()) {
				scan(file);
			}
			else {
				// TODO determine if file is new, changed, or deleted
				this.eventBus.post(new UnknownTrackEvent(file, this.library));
			}
		}
		
		// Update lastIndexedDate to now
		try {
			this.library.setLastIndexedDate(new Date());
			this.db.update(new LibraryUpdateQuery(this.library));
		} catch (DBException e) {
			throw new LibraryIndexerException("Error updating library: " + library, e);
		}
	}

	private boolean isValidDirectory(File directory)
			throws LibraryIndexerException {

		if (!directory.exists()) {
			return false;
		}

		if (!directory.canRead()) {
			return false;
		}

		return true;
	}
}
