package com.perrier.music.coverart;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.perrier.music.config.IConfiguration;
import com.perrier.music.entity.album.AlbumProvider;
import com.perrier.music.entity.track.TrackProvider;
import com.perrier.music.test.MusicUnitTest;

public class CoverArtServiceTest extends MusicUnitTest {

	@Mock
	private IConfiguration configMock;
	@Mock
	private TrackProvider trackProviderMock;
	@Mock
	private AlbumProvider albumProviderMock;

	@InjectMocks
	private CoverArtService coverArtService;

	@Test
	public void shouldConstruct() {
		assertNotNull(this.coverArtService);
	}

}
