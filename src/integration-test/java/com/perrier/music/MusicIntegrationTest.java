package com.perrier.music;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import com.google.inject.Injector;

public class MusicIntegrationTest {

	private static final String TEST_CONFIG_FILE = "conf/test/config.groovy";

	static Injector injector;

	static {
	}

	@BeforeClass
	public static void beforeClass() throws Exception {
		Core core = new Core();
		core.init(TEST_CONFIG_FILE);
		injector = core.getInjector();
	}

	@Before
	public void before() {
		injector.injectMembers(this);
	}

	@After
	public void after() {
	}

	@AfterClass
	public static void afterClass() {
	}

}
