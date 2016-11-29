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
import com.perrier.music.entity.update.TrackGenreUpdater;
import com.perrier.music.entity.update.UpdateResult;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.perrier.music.db.IDatabase;
import com.perrier.music.entity.genre.Genre;
import com.perrier.music.entity.genre.GenreCreateQuery;
import com.perrier.music.entity.genre.GenreFindByNameQuery;
import com.perrier.music.entity.genre.GenreUpdateQuery;
import com.perrier.music.test.MusicUnitTest;

public class TrackGenreUpdaterTest extends MusicUnitTest {

	@Mock
	Track track;

	@Mock
	IDatabase db;

	@InjectMocks
	TrackGenreUpdater trackGenreUpdater;

	Genre genre;

	@Before
	public void before() {
		genre = new Genre();
		genre.setName("nu disco");
		genre.setId(1L);
	}

	@Test
	public void shouldConstruct() {
		assertNotNull(trackGenreUpdater);
	}

	@Test
	public void shouldDoNothingIfTrackHasNoGenreAndInputIsEmpty() throws Exception {

		when(track.getGenre()).thenReturn(null); // original track had no genre

		UpdateResult<Genre> result = trackGenreUpdater.handleUpdate(track, "");

		verify(db, never()).create(isA(GenreCreateQuery.class));
		verify(db, never()).update(isA(GenreUpdateQuery.class));

		assertFalse(result.isCreatedOrDeleted());
		assertNull(result.getUpdate());
	}

	@Test
	public void shouldDoNothingIfTrackHasGenreAndInputIsSame() throws Exception {

		when(track.getGenre()).thenReturn(genre); // original track had genre

		UpdateResult<Genre> result = trackGenreUpdater.handleUpdate(track, "nu disco");

		verify(db, never()).create(isA(GenreCreateQuery.class));
		verify(db, never()).update(isA(GenreUpdateQuery.class));

		assertFalse(result.isCreatedOrDeleted());
		assertNotNull(result.getUpdate());

		assertEquals(genre.getId(), result.getUpdate().getId());
		assertEquals(genre.getName(), result.getUpdate().getName());
	}

	@Test
	public void shouldDoNothingIfTrackHasGenreAndInputIsSameTrimmed() throws Exception {

		when(track.getGenre()).thenReturn(genre); // original track had genre

		UpdateResult<Genre> result = trackGenreUpdater.handleUpdate(track, "\t   nu  disco  \n");

		verify(db, never()).create(isA(GenreCreateQuery.class));
		verify(db, never()).update(isA(GenreUpdateQuery.class));

		assertFalse(result.isCreatedOrDeleted());
		assertNotNull(result.getUpdate());

		assertEquals(genre.getId(), result.getUpdate().getId());
		assertEquals(genre.getName(), result.getUpdate().getName());
	}

	@Test
	public void shouldUpdateGenreIfTrackHasGenreAndInputIsSameIgnoringCase() throws Exception {

		Genre updatedGenre = new Genre();
		updatedGenre.setName("Nu dIsCo");
		updatedGenre.setId(genre.getId());

		when(track.getGenre()).thenReturn(genre); // original track had genre
		when(db.update(isA(GenreUpdateQuery.class))).thenReturn(updatedGenre);

		UpdateResult<Genre> result = trackGenreUpdater.handleUpdate(track, "Nu dIsCo");

		verify(db, never()).create(isA(GenreCreateQuery.class));
		verify(db).update(isA(GenreUpdateQuery.class));

		// track has not changed, just the genre's name (genreId is still the same for track)
		assertFalse(result.isCreatedOrDeleted());
		assertNotNull(result.getUpdate());

		assertEquals(updatedGenre.getName(), result.getUpdate().getName());
		assertEquals(updatedGenre.getId(), result.getUpdate().getId());
	}

	@Test
	public void shouldCreateGenreIfTrackHadNoGenreAndGenreIsNew() throws Exception {

		when(track.getGenre()).thenReturn(null); // original track had no genre
		when(db.find(isA(GenreFindByNameQuery.class))).thenReturn(null); // genre doesn't exist
		when(db.create(isA(GenreCreateQuery.class))).thenReturn(genre); // create new genre

		UpdateResult<Genre> result = trackGenreUpdater.handleUpdate(track, "nu disco");

		verify(db).create(isA(GenreCreateQuery.class));

		assertTrue(result.isCreatedOrDeleted());
		assertNotNull(result.getUpdate());

		assertEquals(genre.getId(), result.getUpdate().getId());
		assertEquals(genre.getName(), result.getUpdate().getName());
	}

	@Test
	public void shouldCreateGenreIfTrackHadGenreAndGenreIsNew() throws Exception {

		Genre newGenre = new Genre();
		newGenre.setName("heavy metal");
		newGenre.setId(2L);

		when(track.getGenre()).thenReturn(genre); // original track had genre
		when(db.find(isA(GenreFindByNameQuery.class))).thenReturn(null); // genre doesn't exist
		when(db.create(isA(GenreCreateQuery.class))).thenReturn(newGenre); // create new genre

		UpdateResult<Genre> result = trackGenreUpdater.handleUpdate(track, "heavy metal");

		verify(db).create(isA(GenreCreateQuery.class));

		assertTrue(result.isCreatedOrDeleted());
		assertNotNull(result.getUpdate());

		assertEquals(newGenre.getId(), result.getUpdate().getId());
		assertEquals(newGenre.getName(), result.getUpdate().getName());
	}

	@Test
	public void shouldCreateGenreIfTrackHadGenreAndNewGenreExists() throws Exception {

		Genre newGenre = new Genre();
		newGenre.setName("heavy metal");
		newGenre.setId(2L);

		when(track.getGenre()).thenReturn(genre); // original track had genre
		when(db.find(isA(GenreFindByNameQuery.class))).thenReturn(newGenre); // genre exists

		UpdateResult<Genre> result = trackGenreUpdater.handleUpdate(track, "heavy metal");

		verify(db, never()).create(isA(GenreCreateQuery.class));
		verify(db, never()).update(isA(GenreUpdateQuery.class));

		assertTrue(result.isCreatedOrDeleted());
		assertNotNull(result.getUpdate());

		assertEquals(newGenre.getId(), result.getUpdate().getId());
		assertEquals(newGenre.getName(), result.getUpdate().getName());
	}

	@Test
	public void shouldRemoveGenre() throws Exception {

		when(track.getGenre()).thenReturn(genre); // original track had genre

		UpdateResult<Genre> result = trackGenreUpdater.handleUpdate(track, null);

		verify(db, never()).create(isA(GenreCreateQuery.class));
		verify(db, never()).update(isA(GenreUpdateQuery.class));

		assertTrue(result.isCreatedOrDeleted());
		assertNull(result.getUpdate());
	}
}
