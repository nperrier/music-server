package com.perrier.music.module;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.perrier.music.auth.LoginAuthenticator;
import com.perrier.music.coverart.CoverArtService;
import com.perrier.music.coverart.ICoverArtService;
import com.perrier.music.db.HibernateConfiguration;
import com.perrier.music.db.HibernateDatabase;
import com.perrier.music.db.IDBConfiguration;
import com.perrier.music.db.IDatabase;
import com.perrier.music.entity.album.AlbumProvider;
import com.perrier.music.entity.artist.ArtistProvider;
import com.perrier.music.entity.genre.GenreProvider;
import com.perrier.music.entity.library.LibraryProvider;
import com.perrier.music.entity.playlist.PlaylistProvider;
import com.perrier.music.entity.track.ITrackAlbumUpdaterFactory;
import com.perrier.music.entity.track.ITrackArtistUpdaterFactory;
import com.perrier.music.entity.track.ITrackGenreUpdaterFactory;
import com.perrier.music.entity.track.ITrackUpdaterFactory;
import com.perrier.music.entity.track.TrackProvider;
import com.perrier.music.indexer.ILibraryIndexerService;
import com.perrier.music.indexer.ILibraryIndexerTaskFactory;
import com.perrier.music.indexer.ILibraryService;
import com.perrier.music.indexer.LibraryIndexerService;
import com.perrier.music.indexer.LibraryService;
import com.perrier.music.search.SearchProvider;
import com.perrier.music.server.IServer;
import com.perrier.music.server.JettyHttpServer;
import com.perrier.music.server.auth.AuthorizationFilterFactory;
import com.perrier.music.tag.ITagParser;
import com.perrier.music.tag.TagParser;

public class MusicModule extends AbstractModule {

	@Override
	public void configure() {

		bind(IDatabase.class).to(HibernateDatabase.class).asEagerSingleton();
		bind(IServer.class).to(JettyHttpServer.class).asEagerSingleton();
		bind(IDBConfiguration.class).to(HibernateConfiguration.class);
		bind(ILibraryIndexerService.class).to(LibraryIndexerService.class).in(Singleton.class);
		bind(ILibraryService.class).to(LibraryService.class).in(Singleton.class);
		bind(ICoverArtService.class).to(CoverArtService.class).in(Singleton.class);
		bind(LoginAuthenticator.class).in(Singleton.class);
		bind(AuthorizationFilterFactory.class).in(Singleton.class);
		bind(ITagParser.class).to(TagParser.class).in(Singleton.class);

		install(new FactoryModuleBuilder().build(ILibraryIndexerTaskFactory.class));

		install(new FactoryModuleBuilder().build(ITrackUpdaterFactory.class));
		install(new FactoryModuleBuilder().build(ITrackArtistUpdaterFactory.class));
		install(new FactoryModuleBuilder().build(ITrackAlbumUpdaterFactory.class));
		install(new FactoryModuleBuilder().build(ITrackGenreUpdaterFactory.class));

		bind(EventBus.class).in(Singleton.class);

		bind(ArtistProvider.class).in(Singleton.class);
		bind(AlbumProvider.class).in(Singleton.class);
		bind(TrackProvider.class).in(Singleton.class);
		bind(LibraryProvider.class).in(Singleton.class);
		bind(GenreProvider.class).in(Singleton.class);
		bind(PlaylistProvider.class).in(Singleton.class);
		bind(SearchProvider.class).in(Singleton.class);

	}

	@Provides
	@Singleton
	private Validator getValidator() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		return validator;
	}
}
