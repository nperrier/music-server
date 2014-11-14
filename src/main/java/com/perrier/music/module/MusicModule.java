package com.perrier.music.module;

import com.google.common.eventbus.EventBus;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Singleton;
import com.google.inject.assistedinject.FactoryModuleBuilder;
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
import com.perrier.music.server.IServer;
import com.perrier.music.server.JettyHttpServer;

public class MusicModule implements Module {

	@Override
	public void configure(Binder binder) {
		binder.bind(IDatabase.class).to(HibernateDatabase.class).asEagerSingleton();
		binder.bind(IServer.class).to(JettyHttpServer.class).asEagerSingleton();
		binder.bind(IDBConfiguration.class).to(HibernateConfiguration.class);
		binder.bind(ILibraryIndexerService.class).to(LibraryIndexerService.class).in(Singleton.class);
		binder.bind(ILibraryService.class).to(LibraryService.class).in(Singleton.class);
		binder.bind(ICoverArtService.class).to(CoverArtService.class).in(Singleton.class);

		binder.install(new FactoryModuleBuilder().build(ILibraryIndexerTaskFactory.class));

		binder.install(new FactoryModuleBuilder().build(ITrackUpdaterFactory.class));
		binder.install(new FactoryModuleBuilder().build(ITrackArtistUpdaterFactory.class));
		binder.install(new FactoryModuleBuilder().build(ITrackAlbumUpdaterFactory.class));
		binder.install(new FactoryModuleBuilder().build(ITrackGenreUpdaterFactory.class));

		binder.bind(EventBus.class).in(Singleton.class);

		binder.bind(ArtistProvider.class).in(Singleton.class);
		binder.bind(AlbumProvider.class).in(Singleton.class);
		binder.bind(TrackProvider.class).in(Singleton.class);
		binder.bind(LibraryProvider.class).in(Singleton.class);
		binder.bind(GenreProvider.class).in(Singleton.class);
		binder.bind(PlaylistProvider.class).in(Singleton.class);

	}
}
