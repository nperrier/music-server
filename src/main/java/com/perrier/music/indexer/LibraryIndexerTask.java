package com.perrier.music.indexer;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.perrier.music.db.DBException;
import com.perrier.music.db.IDatabase;
import com.perrier.music.entity.library.Library;
import com.perrier.music.entity.library.LibraryUpdateQuery;
import com.perrier.music.entity.track.Track;
import com.perrier.music.entity.track.TrackProvider;
import com.perrier.music.indexer.event.ChangedTrackEvent;
import com.perrier.music.indexer.event.ITrackEvent;
import com.perrier.music.indexer.event.UnknownTrackEvent;

public class LibraryIndexerTask implements Callable<Void> {

	private static final Logger log = LoggerFactory.getLogger(LibraryIndexerTask.class);

	// dependencies
	private EventBus bus;
	private TrackProvider trackProvider;
	private IDatabase db;

	private volatile boolean cancelScan = false;

	private static class MusicFileFilter implements FileFilter {

		@Override
		public boolean accept(File f) {
			boolean accept = f.exists() && f.canRead() && (f.isDirectory() || (f.isFile() && f.getName().endsWith(".mp3")));
			return accept;
		}
	}

	private static final FileFilter songFileFilter = new MusicFileFilter();

	// fields
	private final Library library;

	@AssistedInject
	public LibraryIndexerTask(@Assisted
	Library library) {
		this.library = library;
	}

	@Inject
	public void setEventBus(EventBus bus) {
		this.bus = bus;
	}

	@Inject
	public void setTrackProvider(TrackProvider trackProvider) {
		this.trackProvider = trackProvider;
	}

	@Inject
	public void setDb(IDatabase db) {
		this.db = db;
	}

	@Override
	public Void call() throws LibraryIndexerException {

		File rootPath = new File(this.library.getPath());

		if (!rootPath.exists() || !rootPath.canRead()) {
			throw new LibraryIndexerException("Unable to index " + rootPath + ": directory doesn't exist or cannot read");
		}

		// get all indexed files from db for path
		List<Track> tracks = this.trackProvider.findAllByLibraryId(this.library.getId());
		// Map<String, Date> pathToModDate = Maps.newHashMapWithExpectedSize(tracks.size());
		Map<String, Track> pathToTrack = Maps.uniqueIndex(tracks, new Function<Track, String>() {

			@Override
			public String apply(Track track) {
				return track.getPath();
			}
		});

		this.scan(rootPath, pathToTrack);

		// Update lastIndexedDate to now
		try {
			this.library.setLastIndexedDate(new Date());
			this.db.update(new LibraryUpdateQuery(this.library));
		} catch (DBException e) {
			throw new LibraryIndexerException("Error updating library: " + this.library, e);
		}

		return null;
	}

	private void scan(File dir, Map<String, Track> pathToTrack) {

		if (this.cancelScan) {
			return;
		}

		// First, search for all files in dir:
		//
		// * If it has been indexed before (exists) AND its file modification date has not changed (not sure what field to
		// use yet), then continue
		// * If the file mod date has changed, then post IndexEvent.CHANGED
		// * If it has not been indexed before (new), then post IndexEvent.NEW
		// * Remove the existing file from the tracks map
		//
		// Second, loop through the rest of the tracks map and mark all the files as IndexEvent.MISSING
		// NOTE: Don't actually delete the columns! - the tracks may have been relocated
		// Let the User manage cleaning up missing Tracks
		for (File file : dir.listFiles(LibraryIndexerTask.songFileFilter)) {

			// check that we're not being told to stop - called from Executor.shutdownNow()
			if (Thread.currentThread().isInterrupted()) {
				log.debug("Stopping scan: thread was interrupted");
				this.cancelScan = true;
				return;
			}

			if (file.isDirectory()) {
				this.scan(file, pathToTrack);
			} else {
				// TODO Need to consider:
				// standardized paths (slash at the end, etc..)
				// if path is relative or absolute
				// symbolic links
				// collection root path
				// canonical path (resolves sym links)...leave sym links alone?
				try {
					this.index(file, pathToTrack);
				} catch (Exception e) {
					log.warn("Unable to index file: {}", file, e);
				}
			}
		}
	}

	private void index(File file, Map<String, Track> pathToTrack) throws IOException {
		log.debug("Indexing file: " + file);

		String path = file.getCanonicalPath();
		Track track = pathToTrack.get(path);
		ITrackEvent event = null;

		if (track == null) {
			// NEW
			event = new UnknownTrackEvent(file, this.library);
		} else {
			Date fileDate = new Date(file.lastModified());
			if (fileDate.after(track.getModificationDate())) {
				// CHANGED
				event = new ChangedTrackEvent(track, this.library);
				pathToTrack.remove(file);
			}
		}

		this.bus.post(event);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.library == null) ? 0 : this.library.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof LibraryIndexerTask)) {
			return false;
		}
		LibraryIndexerTask other = (LibraryIndexerTask) obj;

		if (this.library == null) {
			if (other.library != null) {
				return false;
			}
		} else if (!this.library.equals(other.library)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "LibraryIndexerTask [library=" + this.library + "]";
	}
}
