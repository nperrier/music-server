package com.perrier.music.db;

import java.util.Set;

import org.h2.Driver;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.perrier.music.ApplicationProperties;
import com.perrier.music.config.IConfiguration;
import com.perrier.music.config.OptionalProperty;
import com.perrier.music.entity.album.Album;
import com.perrier.music.entity.artist.Artist;
import com.perrier.music.entity.genre.Genre;
import com.perrier.music.entity.library.Library;
import com.perrier.music.entity.playlist.Playlist;
import com.perrier.music.entity.playlist.PlaylistTrack;
import com.perrier.music.entity.track.Track;

public class HibernateConfiguration implements IDBConfiguration {

	private final IConfiguration config;

	public static final OptionalProperty<Boolean> SHOW_SQL = new OptionalProperty<Boolean>("h2.showSql", false);

	@Inject
	public HibernateConfiguration(IConfiguration config) {
		this.config = config;
	}

	@Override
	public SessionFactory create() throws DBException {

		Configuration hibernateConfig = new Configuration();

		// TODO Search for these dynamically
		// See: HibernateConfigurationFactory.initializeAnnotations()
		Set<? extends Class<?>> entities = Sets.<Class<?>>newHashSet( //
				Artist.class, //
				Genre.class, //
				Album.class, //
				Track.class, //
				Library.class, //
				Playlist.class, //
				PlaylistTrack.class);

		for (Class<?> entity : entities) {
			hibernateConfig.addAnnotatedClass(entity);
		}

		this.setProperties(hibernateConfig);

		// new org.hibernate.tool.hbm2ddl.SchemaExport(hibernateConfig).create(true, false);

		ServiceRegistry serviceRegistry = new ServiceRegistryBuilder() //
				.applySettings(hibernateConfig.getProperties()) //
				.buildServiceRegistry();

		return hibernateConfig.buildSessionFactory(serviceRegistry);
	}

	private void setProperties(Configuration hibernateConfig) throws DBException {

		hibernateConfig.setProperty(Environment.DRIVER, Driver.class.getName());
		hibernateConfig.setProperty(Environment.DIALECT, H2Dialect.class.getName());
		hibernateConfig.setProperty(Environment.URL, this.config.getRequiredString(ApplicationProperties.URL));
		hibernateConfig.setProperty(Environment.USER, this.config.getRequiredString(ApplicationProperties.USERNAME));
		hibernateConfig.setProperty(Environment.PASS, this.config.getRequiredString(ApplicationProperties.PASSWORD));

		hibernateConfig.setProperty(Environment.AUTOCOMMIT, "true");
		hibernateConfig.setProperty(Environment.SHOW_SQL, this.config.getOptionalBoolean(SHOW_SQL).toString());

		// second-level cache
		// hibernateConfig.setProperty(Environment.USE_SECOND_LEVEL_CACHE, "true");
		// hibernateConfig.setProperty(Environment.CACHE_REGION_FACTORY, EhCacheRegionFactory.class.getName());
		// hibernateConfig.setProperty(Environment.USE_QUERY_CACHE, "true");

		// connection pool
		// hibernateConfig.setProperty(Environment.CONNECTION_PROVIDER, C3P0ConnectionProvider.class.getName());
		hibernateConfig.setProperty(Environment.C3P0_MIN_SIZE, "5");
		hibernateConfig.setProperty(Environment.C3P0_MAX_SIZE, "20");
		hibernateConfig.setProperty(Environment.C3P0_TIMEOUT, "1800"); // 5 min
		hibernateConfig.setProperty(Environment.C3P0_MAX_STATEMENTS, "50");

		// auto-setting of creation & modification dates for entities
		hibernateConfig.setInterceptor(new AuditInterceptor());

	}
}
