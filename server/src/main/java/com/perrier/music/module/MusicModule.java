package com.perrier.music.module;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.perrier.music.ApplicationProperties;
import com.perrier.music.auth.LoginAuthenticator;
import com.perrier.music.config.IConfiguration;
import com.perrier.music.coverart.CoverArtService;
import com.perrier.music.coverart.ICoverArtService;
import com.perrier.music.db.HibernateConfiguration;
import com.perrier.music.db.HibernateDatabase;
import com.perrier.music.db.IDatabase;
import com.perrier.music.entity.album.AlbumProvider;
import com.perrier.music.entity.artist.ArtistProvider;
import com.perrier.music.entity.genre.GenreProvider;
import com.perrier.music.entity.playlist.PlaylistProvider;
import com.perrier.music.entity.track.TrackProvider;
import com.perrier.music.entity.update.AlbumUpdater;
import com.perrier.music.entity.update.TrackAlbumUpdater;
import com.perrier.music.entity.update.TrackArtistUpdater;
import com.perrier.music.entity.update.TrackGenreUpdater;
import com.perrier.music.entity.update.TrackUpdater;
import com.perrier.music.indexer.LibraryService;
import com.perrier.music.search.SearchProvider;
import com.perrier.music.server.JettyHttpServer;
import com.perrier.music.server.auth.AuthorizationFilterFactory;
import com.perrier.music.storage.S3StorageService;

public class MusicModule extends AbstractModule {

	private final IConfiguration config;

	public MusicModule(IConfiguration config) {
		this.config = config;
	}

	@Override
	public void configure() {

		bind(IDatabase.class).to(HibernateDatabase.class).asEagerSingleton();
		bind(JettyHttpServer.class).asEagerSingleton();
		bind(HibernateConfiguration.class);
		bind(ICoverArtService.class).to(CoverArtService.class).in(Singleton.class);
		bind(LoginAuthenticator.class).in(Singleton.class);
		bind(AuthorizationFilterFactory.class).in(Singleton.class);
		bind(LibraryService.class).in(Singleton.class);

		bind(EventBus.class).in(Singleton.class);

		bind(ArtistProvider.class).in(Singleton.class);
		bind(AlbumProvider.class).in(Singleton.class);
		bind(TrackProvider.class).in(Singleton.class);
		bind(GenreProvider.class).in(Singleton.class);
		bind(PlaylistProvider.class).in(Singleton.class);
		bind(SearchProvider.class).in(Singleton.class);

		bind(TrackUpdater.class).in(Singleton.class);
		bind(TrackArtistUpdater.class).in(Singleton.class);
		bind(TrackAlbumUpdater.class).in(Singleton.class);
		bind(TrackGenreUpdater.class).in(Singleton.class);
		bind(AlbumUpdater.class).in(Singleton.class);
	}

	@Provides
	@Singleton
	private Validator getValidator() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		return validator;
	}

	@Provides
	@Singleton
	private S3StorageService getStorage() {
		String accessKeyId = this.config.getRequiredString(ApplicationProperties.AWS_ACCESS_KEY_ID);
		String secretAccessKey = this.config.getRequiredString(ApplicationProperties.AWS_SECRET_ACCESS_KEY);
		String bucket = this.config.getRequiredString(ApplicationProperties.AWS_BUCKET);
		String region = this.config.getRequiredString(ApplicationProperties.AWS_REGION);

		S3StorageService storageService = new S3StorageService(accessKeyId, secretAccessKey, bucket, region);
		return storageService;
	}
}
