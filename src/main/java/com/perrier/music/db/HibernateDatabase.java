package com.perrier.music.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.jdbc.Work;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.AbstractIdleService;
import com.google.inject.Inject;

public class HibernateDatabase extends AbstractIdleService implements IDatabase {

	private static final Logger log = LoggerFactory.getLogger(HibernateDatabase.class);

	private final IDBConfiguration config;
	private Persistence persistence;

	@Inject
	public HibernateDatabase(IDBConfiguration config) {
		this.config = config;
	}

	@Override
	protected void startUp() throws Exception {
		// A SessionFactory is set up once for an application
		try {
			SessionFactory sessionFactory = config.create();
			SessionManager sessionManager = new SessionManager(sessionFactory);
			this.persistence = new Persistence(sessionManager);
		} catch (Exception e) {
			throw new DBException("Error creating config", e);
		}
	}

	@Override
	protected void shutDown() throws Exception {
		this.shutdownDatabase(); // h2 first
		this.persistence.stop(); // hibernate second
	}

	private void shutdownDatabase() {
		// H2 requires a shutdown attempt
		try {
			Session session = this.openSession();
			session.doWork(new Work() {

				@Override
				public void execute(Connection conn) throws SQLException {
					Statement stmnt = conn.createStatement();
					stmnt.execute("shutdown");
					stmnt.close();
				}
			});
		} catch (Exception e) {
			log.info("Failed to shut down H2. Shutdown will continue", e);
		}

		log.info("Shutdown: H2 closed successfully");
	}

	public <T> T find(FindQuery<T> query) throws DBException {
		try {
			query.setDb(this);
			Session session = this.openSession();
			T result = query.query(session);
			return result;
		} catch (DBException e) {
			throw e;
		} catch (Exception e) {
			throw new DBException("Find Error: ", e);
		} finally {
			this.closeSession();
		}
	}

	public <T> T create(CreateQuery<T> query) throws DBException {
		try {
			query.setDb(this);
			Session session = this.beginTransaction();
			T result = query.query(session);
			this.commit();
			return result;
		} catch (DBException e) {
			this.rollback();
			throw e;
		} catch (Exception e) {
			this.rollback();
			throw new DBException("Insert Error: ", e);
		} finally {
			this.endTransaction();
		}
	}

	public void delete(DeleteQuery query) throws DBException {
		try {
			query.setDb(this);
			Session session = this.beginTransaction();
			query.query(session);
			this.commit();
		} catch (DBException e) {
			this.rollback();
			throw e;
		} catch (Exception e) {
			this.rollback();
			throw new DBException("Delete Error: ", e);
		} finally {
			this.endTransaction();
		}
	}

	public <T> T update(UpdateQuery<T> query) throws DBException {
		try {
			query.setDb(this);
			Session session = this.beginTransaction();
			T result = query.query(session);
			this.commit();
			return result;
		} catch (DBException e) {
			this.rollback();
			throw e;
		} catch (Exception e) {
			this.rollback();
			throw new DBException("Update Error: ", e);
		} finally {
			this.endTransaction();
		}
	}

	public <T> T forceUpdate(ForceUpdateQuery<T> query) throws DBException {
		try {
			query.setDb(this);
			Session session = this.beginTransaction();
			T result = query.query(session);
			this.commit();
			return result;
		} catch (DBException e) {
			this.rollback();
			throw e;
		} catch (Exception e) {
			this.rollback();
			throw new DBException("Insert or Update Error: ", e);
		} finally {
			this.endTransaction();
		}
	}

	public boolean commit() throws DBException {
		try {
			final boolean commit = this.persistence.commit();
			return commit;
		} catch (HibernateException e) {
			throw new DBException("Unable to commit", e);
		}
	}

	public boolean rollback() {
		final boolean rollback = this.persistence.rollback();
		return rollback;
	}

	public Session beginTransaction() throws DBException {
		try {
			final Session s = this.persistence.beginTransaction();
			return s;
		} catch (HibernateException e) {
			throw new DBException("Unable to begin transaction", e);
		}
	}

	public boolean endTransaction() {
		final boolean end = this.persistence.endTransaction();
		return end;
	}

	private Session openSession() throws DBException {
		try {
			final Session s = this.persistence.openSession();
			return s;
		} catch (HibernateException e) {
			throw new DBException("Unable to open session", e);
		}
	}

	private void closeSession() {
		this.persistence.closeSession();
	}

}
