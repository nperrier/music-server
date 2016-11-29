package com.perrier.music.entity.update;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.perrier.music.db.IDatabase;
import com.perrier.music.entity.album.Album;
import com.perrier.music.test.MusicUnitTest;

import static org.junit.Assert.assertNotNull;

public class AlbumUpdaterTest extends MusicUnitTest {

	@Mock
	IDatabase db;

	@InjectMocks
	AlbumUpdater albumUpdater;

	Album album;

	@Before
	public void before() {
		album = new Album();
		album.setName("the college dropout");
		album.setId(1L);
	}

	@Test
	public void shouldConstruct() {
		assertNotNull(albumUpdater);
	}
}
