package com.perrier.music.entity.update;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.perrier.music.entity.track.Track;
import com.perrier.music.entity.update.TrackArtistUpdater;
import com.perrier.music.entity.update.UpdateResult;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.perrier.music.db.IDatabase;
import com.perrier.music.entity.artist.Artist;
import com.perrier.music.entity.artist.ArtistCreateQuery;
import com.perrier.music.entity.artist.ArtistFindByNameQuery;
import com.perrier.music.entity.artist.ArtistUpdateQuery;
import com.perrier.music.test.MusicUnitTest;

public class TrackArtistUpdaterTest extends MusicUnitTest {

	@Mock
	Track track;

	@Mock
	IDatabase db;

	@InjectMocks
	TrackArtistUpdater trackArtistUpdater;

	Artist artist;

	@Before
	public void before() {
		artist = new Artist();
		artist.setName("kanye west");
		artist.setId(1L);
	}

	@Test
	public void shouldConstruct() {
		assertNotNull(trackArtistUpdater);
	}

	@Test
	public void shouldDoNothingIfTrackHasNoArtistAndInputIsEmpty() throws Exception {

		when(track.getArtist()).thenReturn(null); // original track had no artist

		UpdateResult<Artist> result = trackArtistUpdater.handleUpdate(track, "");

		verify(db, never()).create(isA(ArtistCreateQuery.class));
		verify(db, never()).update(isA(ArtistUpdateQuery.class));

		assertFalse(result.isCreatedOrDeleted());
		assertNull(result.getUpdate());
	}

	@Test
	public void shouldDoNothingIfTrackHasArtistAndInputIsSame() throws Exception {

		when(track.getArtist()).thenReturn(artist); // original track had artist

		UpdateResult<Artist> result = trackArtistUpdater.handleUpdate(track, "kanye west");

		verify(db, never()).create(isA(ArtistCreateQuery.class));
		verify(db, never()).update(isA(ArtistUpdateQuery.class));

		assertFalse(result.isCreatedOrDeleted());
		assertNotNull(result.getUpdate());

		assertEquals(artist.getId(), result.getUpdate().getId());
		assertEquals(artist.getName(), result.getUpdate().getName());
	}

	@Test
	public void shouldDoNothingIfTrackHasArtistAndInputIsSameTrimmed() throws Exception {

		when(track.getArtist()).thenReturn(artist); // original track had artist

		UpdateResult<Artist> result = trackArtistUpdater.handleUpdate(track, "\t   kanye   west  \n");

		verify(db, never()).create(isA(ArtistCreateQuery.class));
		verify(db, never()).update(isA(ArtistUpdateQuery.class));

		assertFalse(result.isCreatedOrDeleted());
		assertNotNull(result.getUpdate());

		assertEquals(artist.getId(), result.getUpdate().getId());
		assertEquals(artist.getName(), result.getUpdate().getName());
	}

	@Test
	public void shouldUpdateArtistIfTrackHasArtistAndInputIsSameIgnoringCase() throws Exception {

		Artist updatedArtist = new Artist();
		updatedArtist.setName("KaNyE wEsT");
		updatedArtist.setId(1L);

		when(track.getArtist()).thenReturn(artist); // original track had artist
		when(db.update(isA(ArtistUpdateQuery.class))).thenReturn(updatedArtist);

		UpdateResult<Artist> result = trackArtistUpdater.handleUpdate(track, "KaNyE wEsT");

		verify(db, never()).create(isA(ArtistCreateQuery.class));
		verify(db).update(isA(ArtistUpdateQuery.class));

		// track has not changed, just the artist's name (artistId is still the same for track)
		assertFalse(result.isCreatedOrDeleted());
		assertNotNull(result.getUpdate());

		assertEquals(updatedArtist.getId(), result.getUpdate().getId());
		assertEquals(updatedArtist.getName(), result.getUpdate().getName());
	}

	@Test
	public void shouldCreateArtistIfTrackHadNoArtistAndArtistIsNew() throws Exception {

		when(track.getArtist()).thenReturn(null); // original track had no artist
		when(db.find(isA(ArtistFindByNameQuery.class))).thenReturn(null); // artist doesn't exist
		when(db.create(isA(ArtistCreateQuery.class))).thenReturn(artist); // create new artist

		UpdateResult<Artist> result = trackArtistUpdater.handleUpdate(track, "kanye west");

		verify(db).create(isA(ArtistCreateQuery.class));

		assertTrue(result.isCreatedOrDeleted());
		assertNotNull(result.getUpdate());

		assertEquals(artist.getId(), result.getUpdate().getId());
		assertEquals(artist.getName(), result.getUpdate().getName());
	}

	@Test
	public void shouldCreateArtistIfTrackHadArtistAndArtistIsNew() throws Exception {

		Artist newArtist = new Artist();
		newArtist.setName("rick james");
		newArtist.setId(2L);

		when(track.getArtist()).thenReturn(artist); // original track had artist
		when(db.find(isA(ArtistFindByNameQuery.class))).thenReturn(null); // artist doesn't exist
		when(db.create(isA(ArtistCreateQuery.class))).thenReturn(newArtist); // create new artist

		UpdateResult<Artist> result = trackArtistUpdater.handleUpdate(track, "rick james");

		verify(db).create(isA(ArtistCreateQuery.class));

		assertTrue(result.isCreatedOrDeleted());
		assertNotNull(result.getUpdate());

		assertEquals(newArtist.getId(), result.getUpdate().getId());
		assertEquals(newArtist.getName(), result.getUpdate().getName());
	}

	@Test
	public void shouldCreateArtistIfTrackHadArtistAndNewArtistExists() throws Exception {

		Artist newArtist = new Artist();
		newArtist.setName("rick james");
		newArtist.setId(2L);

		when(track.getArtist()).thenReturn(artist); // original track had artist
		when(db.find(isA(ArtistFindByNameQuery.class))).thenReturn(newArtist); // artist exists

		UpdateResult<Artist> result = trackArtistUpdater.handleUpdate(track, "rick james");

		verify(db, never()).create(isA(ArtistCreateQuery.class));
		verify(db, never()).update(isA(ArtistUpdateQuery.class));

		assertTrue(result.isCreatedOrDeleted());
		assertNotNull(result.getUpdate());

		assertEquals(newArtist.getId(), result.getUpdate().getId());
		assertEquals(newArtist.getName(), result.getUpdate().getName());
	}

	@Test
	public void shouldRemoveArtist() throws Exception {

		when(track.getArtist()).thenReturn(artist); // original track had artist

		UpdateResult<Artist> result = trackArtistUpdater.handleUpdate(track, null);

		verify(db, never()).create(isA(ArtistCreateQuery.class));
		verify(db, never()).update(isA(ArtistUpdateQuery.class));

		assertTrue(result.isCreatedOrDeleted());
		assertNull(result.getUpdate());
	}
}
