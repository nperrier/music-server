package com.perrier.music.indexer;

import static org.junit.Assert.fail;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Before;
import org.junit.Test;

import com.perrier.music.entity.library.Library;
import com.perrier.music.test.MusicUnitTest;

/**
 * Sanity test - ensuring that tasks can be interrupted and cease processing in the middle of operations
 */
public class LibraryIndexerServiceTest extends MusicUnitTest {

	ILibraryIndexerService libraryIndexerService;

	@Before
	public void before() {
		this.libraryIndexerService = new LibraryIndexerService();
	}

	@Test
	public void test() {

		try {
			this.libraryIndexerService.startAsync().awaitRunning(5, TimeUnit.SECONDS);
		} catch (TimeoutException e) {
			fail();
		}

		class TestTask extends LibraryIndexerTask {

			public TestTask(Library library) {
				super(library);
			}

			@Override
			public Void call() throws LibraryIndexerException {
				try {
					while (!Thread.currentThread().isInterrupted()) {
						System.out.println("Sleeping 2 seconds...");
						Thread.sleep(2000);
					}
				} catch (InterruptedException e) {
					System.out.println("Thread was interrupted!");
				}

				return null;
			}
		}
		;

		this.libraryIndexerService.submit(new TestTask(null));

		try {
			Thread.sleep(6000);
			System.out.println("Shutting down service now");
			this.libraryIndexerService.stopAsync().awaitTerminated(5, TimeUnit.SECONDS);

		} catch (TimeoutException e) {
			fail();
		} catch (InterruptedException e) {
			fail();
		}
	}

}