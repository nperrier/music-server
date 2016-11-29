package com.perrier.music.indexer;

import java.io.File;
import java.nio.file.Files;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.perrier.music.MusicIntegrationTest;
import com.perrier.music.coverart.ICoverArtService;
import com.perrier.music.db.IDatabase;
import com.perrier.music.entity.album.AlbumProvider;
import com.perrier.music.entity.artist.Artist;
import com.perrier.music.entity.artist.ArtistFindByNameQuery;
import com.perrier.music.entity.artist.ArtistProvider;
import com.perrier.music.entity.genre.GenreProvider;
import com.perrier.music.entity.library.Library;
import com.perrier.music.entity.library.LibraryCreateQuery;
import com.perrier.music.indexer.event.UnknownTrackEvent;
import com.perrier.music.tag.ITagParser;
import com.perrier.music.tag.MockTag;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LibraryServiceIntegTest extends MusicIntegrationTest {

	@Inject
	IDatabase db;

	@Inject
	EventBus bus;

	@Inject
	ICoverArtService coverArtService;

	@Inject
	GenreProvider genreProvider;

	@Inject
	AlbumProvider albumProvider;

	@Inject
	ArtistProvider artistProvider;

	ITagParser tagParser;

	LibraryService libraryService;

	Library lib;

	@Before
	public void setup() {
		// don't want to read from file system. Create testing tags
		tagParser = mock(ITagParser.class);

		this.libraryService = new LibraryService(db, bus, coverArtService, genreProvider, albumProvider, artistProvider,
				tagParser);
		lib = new Library();
		lib.setPath("/some/fake/path");
		this.db.create(new LibraryCreateQuery(lib));
	}

	@Test
	public void unknownTrack_nullTag() throws Exception {
		File mp3File = Files.createTempFile("test", ".mp3").toFile();
		MockTag tag = new MockTag.Builder().build();

		when(this.tagParser.parseTag(eq(mp3File))).thenReturn(tag);

		UnknownTrackEvent event = new UnknownTrackEvent(mp3File, lib);

		libraryService.handle(event);
	}

	@Ignore
	@Test
	public void unknownTrack_artistOnlyTag() throws Exception {
		File mp3File = Files.createTempFile("test", ".mp3").toFile();
		MockTag tag = new MockTag.Builder() //
				.artist("artist name") //
				.build();

		when(this.tagParser.parseTag(eq(mp3File))).thenReturn(tag);

		UnknownTrackEvent event = new UnknownTrackEvent(mp3File, lib);

		libraryService.handle(event);

		// should have created an artist
		Artist artist = db.find(new ArtistFindByNameQuery("artist name"));
		assertNotNull(artist);
	}
}
