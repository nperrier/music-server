package com.perrier.music.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.jdbc.Work;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class controls sessions on a per-thread basis. They are created and managed internally to this class.
 * 
 * This class was created to separate the management of the objects from the reference-counting that the Persistence
 * class must do in order to figure out when those operations should be performed.
 * 
 */
public class SessionManager {

	private final static Logger log = LoggerFactory.getLogger(SessionManager.class.getName());

	private final SessionFactory sessionFactory;
	private final ThreadLocal<ThreadSession> threadObject = new ThreadLocal<ThreadSession>();

	public SessionManager(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	/**
	 * Get the current thread's top ThreadSession. Create if not found.
	 * 
	 * @return
	 */
	public ThreadSession getThreadSession() {
		ThreadSession ts = this.getExistingThreadSession();
		if (ts == null) {
			ts = this.createDefaultThreadSession();
			this.threadObject.set(ts);
		}
		return ts;
	}

	/**
	 * Retrieve the current ThreadSession without creating one if it's not found. May return null.
	 * 
	 * @return
	 */
	public ThreadSession getExistingThreadSession() {
		return this.threadObject.get();
	}

	/**
	 * Create the standard ThreadSession object.
	 * 
	 * This is done in multiple places, so this method exists to make sure it's always done the same way.
	 * 
	 * @return
	 */
	private ThreadSession createDefaultThreadSession() {
		return new ThreadSession(this.openSession());
	}

	/**
	 * Close the Session and cleanup the ThreadSession.
	 * 
	 * @param threadSession
	 */
	public void closeThreadSession() {
		ThreadSession ts = this.getExistingThreadSession();
		if (ts == null) {
			log.warn("Attempting to close a ThreadSession without a current session");
			return;
		}

		try {
			Session session = ts.getSession();

			if ((session != null) && session.isOpen()) {
				try {
					// manually send a rollback before handing back the connection
					session.doWork(new Work() {

						@Override
						public void execute(Connection connection) throws SQLException {
							Statement st = connection.createStatement();
							st.execute("rollback");
							st.close();
						}
					});
				} finally {
					session.close();
				}
			}
		} catch (Exception e) {
			log.warn("Unexpected error while closing a session.", e);
		} finally {
			if (this.threadObject.get() != null) {
				this.threadObject.remove();
			}
		}
	}

	/**
	 * Return a session from hibernate
	 * 
	 * @return
	 */
	private Session openSession() throws HibernateException {
		Session session = null;
		try {
			session = this.getSessionFactory().openSession();
			session.setFlushMode(FlushMode.MANUAL);
		} catch (HibernateException he) {
			log.warn("Failed to open a session", he);
			throw he;
		} catch (Exception e) {
			log.warn("Failed to open a session with unknown exception", e);
			throw new RuntimeException("Unable to retrieve a session", e);
		}

		return session;
	}

	/**
	 * Helper class to create a session factory.
	 * 
	 * @return
	 * @throws Exception
	 */
	private SessionFactory getSessionFactory() throws Exception {
		return this.sessionFactory;
	}

	/**
	 * Stop all sessions. Close the session factory.
	 */
	public void stop() {
		this.sessionFactory.close();
	}
}
