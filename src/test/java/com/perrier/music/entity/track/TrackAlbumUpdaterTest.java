package com.perrier.music.entity.track;

import static org.junit.Assert.*;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.perrier.music.db.IDatabase;
import com.perrier.music.entity.album.Album;
import com.perrier.music.entity.album.AlbumCreateQuery;
import com.perrier.music.entity.album.AlbumFindByNameAndArtistIdQuery;
import com.perrier.music.entity.album.AlbumUpdateQuery;
import com.perrier.music.entity.artist.Artist;
import com.perrier.music.entity.artist.ArtistCreateQuery;
import com.perrier.music.entity.artist.ArtistUpdateQuery;
import com.perrier.music.entity.track.AbstractTrackUpdater.UpdateResult;
import com.perrier.music.test.MusicUnitTest;

public class TrackAlbumUpdaterTest extends MusicUnitTest {

	@Mock
	Track track;

	@Mock
	UpdateResult<Artist> artistUpdate;

	@Mock
	UpdateResult<Artist> albumArtistUpdate;

	@Mock
	IDatabase db;

	@InjectMocks
	TrackAlbumUpdater trackAlbumUpdater;

	Album album;

	@Before
	public void before() {
		album = new Album();
		album.setName("the college dropout");
		album.setId(1L);
	}

	@Test
	public void shouldConstruct() {
		assertNotNull(trackAlbumUpdater);
	}

	@Test
	public void shouldDoNothingIfTrackHasNoAlbumAndInputIsEmpty() throws Exception {

		when(track.getAlbum()).thenReturn(null); // original track had no album

		UpdateResult<Album> result = trackAlbumUpdater.handleUpdate("");

		verify(db, never()).create(isA(AlbumCreateQuery.class));
		verify(db, never()).update(isA(AlbumUpdateQuery.class));

		assertFalse(result.isCreatedOrDeleted());
		assertNull(result.getUpdate());
	}

	@Test
	public void shouldDoNothingIfTrackHasAlbumAndInputIsSame() throws Exception {

		when(track.getAlbum()).thenReturn(album); // original track had album
		when(artistUpdate.isCreatedOrDeleted()).thenReturn(false);
		when(albumArtistUpdate.isDeleted()).thenReturn(false);

		UpdateResult<Album> result = trackAlbumUpdater.handleUpdate("the college dropout");

		verify(db, never()).create(isA(AlbumCreateQuery.class));
		verify(db, never()).update(isA(AlbumUpdateQuery.class));

		assertFalse(result.isCreatedOrDeleted());
		assertSame(album, result.getUpdate());
	}

	@Test
	public void shouldDoNothingIfTrackHasAlbumAndInputIsSameTrimmed() throws Exception {

		when(track.getAlbum()).thenReturn(album); // original track had album

		UpdateResult<Album> result = trackAlbumUpdater.handleUpdate("\t   the  college   dropout  \n");

		verify(db, never()).create(isA(AlbumCreateQuery.class));
		verify(db, never()).update(isA(AlbumUpdateQuery.class));

		assertFalse(result.isCreatedOrDeleted());
		assertSame(album, result.getUpdate());
	}

	@Test
	public void shouldUpdateAlbumIfTrackHasAlbumAndInputIsSameIgnoringCase() throws Exception {

		Album updatedAlbum = new Album();
		updatedAlbum.setName("tHe CoLLeGe DroPoUt");
		updatedAlbum.setId(1L);

		when(track.getAlbum()).thenReturn(album); // original track had album
		when(db.update(isA(AlbumUpdateQuery.class))).thenReturn(updatedAlbum);

		UpdateResult<Album> result = trackAlbumUpdater.handleUpdate("tHe CoLLeGe DroPoUt");

		verify(db, never()).create(isA(AlbumCreateQuery.class));
		verify(db).update(isA(AlbumUpdateQuery.class));

		// track has not changed, just the album's name (albumId is still the same for track)
		assertFalse(result.isCreatedOrDeleted());
		assertNotNull(result.getUpdate());

		assertEquals("tHe CoLLeGe DroPoUt", result.getUpdate().getName());
		assertEquals(Long.valueOf(1L), result.getUpdate().getId());
	}

	@Test
	public void shouldCreateAlbumIfTrackHadNoAlbumAndAlbumIsNew() throws Exception {

		when(track.getAlbum()).thenReturn(null); // original track had no album
		when(db.create(isA(AlbumCreateQuery.class))).thenReturn(album); // create new album

		UpdateResult<Album> result = trackAlbumUpdater.handleUpdate("the college dropout");

		verify(db).create(isA(AlbumCreateQuery.class));

		assertTrue(result.isCreatedOrDeleted());
		assertSame(album, result.getUpdate());
	}

	@Test
	public void shouldCreateAlbumIfTrackHadAlbumAndAlbumIsNew() throws Exception {

		Album newAlbum = new Album();
		newAlbum.setName("late registration");
		newAlbum.setId(2L);

		when(track.getAlbum()).thenReturn(album); // original track had album
		when(db.find(isA(AlbumFindByNameAndArtistIdQuery.class))).thenReturn(null); // album doesn't exist
		when(db.create(isA(AlbumCreateQuery.class))).thenReturn(newAlbum); // create new album

		UpdateResult<Album> result = trackAlbumUpdater.handleUpdate("late registration");

		assertTrue(result.isCreatedOrDeleted());
		assertSame(newAlbum, result.getUpdate());
	}

	@Test
	public void shouldCreateAlbumIfTrackHadAlbumAndNewAlbumExists() throws Exception {

		Album newAlbum = new Album();
		newAlbum.setName("late registration");
		newAlbum.setId(2L);

		when(track.getAlbum()).thenReturn(album); // original track had album
		when(db.find(isA(AlbumFindByNameAndArtistIdQuery.class))).thenReturn(newAlbum); // album exists

		UpdateResult<Album> result = trackAlbumUpdater.handleUpdate("late registration");

		verify(db, never()).create(isA(ArtistCreateQuery.class));
		verify(db, never()).update(isA(ArtistUpdateQuery.class));

		assertTrue(result.isCreatedOrDeleted());
		assertSame(newAlbum, result.getUpdate());
	}

	@Test
	public void shouldRemoveAlbum() throws Exception {

		when(track.getAlbum()).thenReturn(album); // original track had album

		UpdateResult<Album> result = trackAlbumUpdater.handleUpdate(null);

		verify(db, never()).create(isA(AlbumCreateQuery.class));
		verify(db, never()).update(isA(AlbumUpdateQuery.class));

		assertTrue(result.isCreatedOrDeleted());
		assertNull(result.getUpdate());
	}

	/* ********** Tests below assume the track artist has changed ************* */

	@Test
	public void shouldAssignExistingAlbumIfArtistRemovedButAlbumWasSame() throws Exception {
		// track-artist: removed
		// track-album: no change - will update with existing album for new artist
		Album existingAlbum = new Album();
		existingAlbum.setName(album.getName());
		existingAlbum.setId(2L);

		when(track.getAlbum()).thenReturn(album); // original track had album
		when(artistUpdate.isCreatedOrDeleted()).thenReturn(true);
		when(db.find(isA(AlbumFindByNameAndArtistIdQuery.class))).thenReturn(existingAlbum);

		UpdateResult<Album> result = trackAlbumUpdater.handleUpdate("kanye west");

		verify(db, never()).update(isA(AlbumUpdateQuery.class));
		verify(db, never()).create(isA(AlbumCreateQuery.class));

		assertTrue(result.isCreatedOrDeleted());
		assertNotNull(result.getUpdate());

		assertEquals(existingAlbum.getName(), result.getUpdate().getName());
		assertEquals(existingAlbum.getId(), result.getUpdate().getId());
		assertNull(result.getUpdate().getArtist());
	}

	@Test
	public void shouldCreateAlbumIfArtistChangedButAlbumWasSame() throws Exception {
		// track-artist: changed
		Artist trackArtist = new Artist();
		trackArtist.setName("jay-z");
		trackArtist.setId(2L);

		// track-album: no change
		Album newAlbum = new Album();
		newAlbum.setName(album.getName());
		newAlbum.setId(2L);
		newAlbum.setArtist(trackArtist);

		when(track.getAlbum()).thenReturn(album); // original track had album
		when(artistUpdate.isCreatedOrDeleted()).thenReturn(true);
		when(artistUpdate.getUpdate()).thenReturn(trackArtist);

		when(db.create(isA(AlbumCreateQuery.class))).thenReturn(newAlbum);

		UpdateResult<Album> result = trackAlbumUpdater.handleUpdate("kanye west");

		verify(db, never()).update(isA(AlbumUpdateQuery.class));

		assertTrue(result.isCreatedOrDeleted());
		assertNotNull(result.getUpdate());

		assertEquals(newAlbum.getName(), result.getUpdate().getName());
		assertEquals(newAlbum.getId(), result.getUpdate().getId());
		assertSame(newAlbum.getArtist(), result.getUpdate().getArtist());
	}

	@Test
	public void shouldCreateAlbumIfArtistRemovedAndAlbumChanged() throws Exception {
		// track-artist: removed
		// track-album: changed (created)
		Album newAlbum = new Album();
		newAlbum.setName(album.getName());
		newAlbum.setId(2L);

		when(track.getAlbum()).thenReturn(null); // original track had no album
		when(artistUpdate.isCreatedOrDeleted()).thenReturn(true);
		when(db.create(isA(AlbumCreateQuery.class))).thenReturn(newAlbum);

		UpdateResult<Album> result = trackAlbumUpdater.handleUpdate("the college dropout");

		verify(db, never()).update(isA(AlbumUpdateQuery.class));

		assertTrue(result.isCreatedOrDeleted());
		assertNotNull(result.getUpdate());

		assertEquals(newAlbum.getName(), result.getUpdate().getName());
		assertEquals(newAlbum.getId(), result.getUpdate().getId());
		assertNull(result.getUpdate().getArtist());
	}

	@Test
	public void shouldAssignExistingAlbumIfArtistChangedAndAlbumChanged() throws Exception {
		// track-artist: changed
		Artist trackArtist = new Artist();
		trackArtist.setName("jay-z");
		trackArtist.setId(2L);

		// track-album: changed
		Album existingAlbum = new Album();
		existingAlbum.setName("late registration");
		existingAlbum.setId(2L);
		existingAlbum.setArtist(trackArtist);

		when(track.getAlbum()).thenReturn(album); // original track had album
		when(artistUpdate.isCreatedOrDeleted()).thenReturn(true);
		when(artistUpdate.getUpdate()).thenReturn(trackArtist);
		when(db.find(isA(AlbumFindByNameAndArtistIdQuery.class))).thenReturn(existingAlbum);

		UpdateResult<Album> result = trackAlbumUpdater.handleUpdate("late registration");

		verify(db, never()).create(isA(AlbumCreateQuery.class));
		verify(db, never()).update(isA(AlbumUpdateQuery.class));

		assertTrue(result.isCreatedOrDeleted());
		assertNotNull(result.getUpdate());

		assertEquals(existingAlbum.getName(), result.getUpdate().getName());
		assertEquals(existingAlbum.getId(), result.getUpdate().getId());
		assertSame(trackArtist, result.getUpdate().getArtist());
	}
}
