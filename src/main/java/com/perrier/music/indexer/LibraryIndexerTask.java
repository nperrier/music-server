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
import com.perrier.music.entity.library.Library;
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
	
	private static class MusicFileFilter implements FileFilter {
		@Override
		public boolean accept(File f) {
			boolean accept = f.exists() && f.canRead() &&
					(f.isDirectory() || (f.isFile() && f.getName().endsWith(".mp3")));
			return accept;
		}
	}
	
	private static final FileFilter songFileFilter = new MusicFileFilter();
	
	// fields
	private final Library library;
	
	@AssistedInject
	public LibraryIndexerTask(@Assisted Library library) {
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
	
	@Override
	public Void call() throws LibraryIndexerException {

		File rootPath = new File(this.library.getPath());
		
		if(!rootPath.exists() || !rootPath.canRead()) {
			throw new LibraryIndexerException("Unable to index " + rootPath + ": directory doesn't exist or cannot read");
		}

		// get all indexed files from db for path
		List<Track> tracks = this.trackProvider.findAllByLibraryId(library.getId());
		//Map<String, Date> pathToModDate = Maps.newHashMapWithExpectedSize(tracks.size());
		Map<String, Track> pathToTrack = Maps.uniqueIndex(tracks, new Function<Track, String>() {
			@Override
			public String apply(Track track) {
				return track.getPath();
			}
		});
		
		scan(rootPath, pathToTrack);
		
//		try {
//			for(int i = 1; i <= 10; i++) {
//			log.info("Scanning path: " + path + " for " + i + " seconds");
//				Thread.sleep(1000);
//			}
//		} catch (InterruptedException e) {
//			log.debug("{} was interrupted", CollectionIndexerService.class.getSimpleName());
//			e.printStackTrace();
//		}
		
		return null;
	}

	private void scan(File dir, Map<String, Track> pathToTrack) {
		
		/* 
		 * First, search for all files in dir:
		 * * If it has been indexed before (exists) AND its file modification date has not changed (not sure what field to use yet)
		 * * Then continue
		 * * If the file mod date has changed, Then post IndexEvent.CHANGED
		 * * If it has not been indexed before (new), Then post IndexEvent.NEW
		 * Remove the existing file from the tracks map
		 * 
		 * Second, loop through the rest of the tracks map and mark all the files as IndexEvent.MISSING
		 * * Don't actually delete the columns! The tracks may have been relocated
		 * * Let the User manage cleaning up missing Tracks
		 */
		
		for(File file : dir.listFiles(LibraryIndexerTask.songFileFilter)) {
			if (file.isDirectory()) {
				scan(file, pathToTrack);
			}
			else {
				// TODO Need to consider:
				// standardized paths (slash at the end, etc..)
				// if path is relative or absolute
				// symbolic links
				// collection root path
				// canonical is supposed to resolve sym links...this is what
				// should be stored in db
				try {
					index(file, pathToTrack);
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
		result = prime * result
				+ ((library == null) ? 0 : library.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof LibraryIndexerTask))
			return false;
		LibraryIndexerTask other = (LibraryIndexerTask) obj;
		
		if (library == null) {
			if (other.library != null)
				return false;
		} else if (!library.equals(other.library))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "LibraryIndexerTask [library=" + library + "]";
	}
}
