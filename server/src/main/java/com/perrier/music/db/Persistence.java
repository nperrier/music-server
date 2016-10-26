package com.perrier.music.db;

import org.hibernate.CacheMode;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.resource.transaction.spi.TransactionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides transactions and sessions to desperate, clamoring clients. Sessions are automatically rolled into the parent
 * transaction if it exists FOR THE THREAD. All factory work is hidden from the client.
 * <p>
 * A client class should call these methods in one of two ways. If you are not interested in a transaction, then merely
 * call getSession and closeSession (always close in a finally block). Methods will automatically use the session from
 * their caller method if it exists. Reference counting guarantees the session will not close until the parent does so,
 * which makes it incredibly important to close in a finally block.
 * <p>
 * The second method, involving a transaction, uses beginTransaction, commit/rollback, and endTransaction (in a finally
 * block). Child methods will automatically be enrolled into the parent's transaction.
 * <p>
 * Both transactions and session calls can be nested indiscriminately.
 */
public class Persistence {

	private final static Logger log = LoggerFactory.getLogger(Persistence.class);

	private final SessionManager sessionManager;

	/**
	 * Call once at system start. Must be called before any sessions can be provided.
	 *
	 * @param sessionManager
	 */
	public Persistence(SessionManager sessionManager) {
		this.sessionManager = sessionManager;
	}

	public void stop() {
		sessionManager.stop();
	}

	public void logSession(String s) {
		ThreadSession ts = sessionManager.getExistingThreadSession();
		log.info("ThreadSession::" + s + "; " + ts);
	}

	/**
	 * Lazily get the session for your thread of control. One will be created if necessary.
	 *
	 * @return Session
	 * @throws HibernateException
	 */
	public Session openSession() throws HibernateException {
		ThreadSession ts = sessionManager.getThreadSession();
		ts.incrementSessionOpenCount();
		return ts.getSession();
	}

	/**
	 * Close the session IF the caller is the one that opened it.
	 * <p>
	 * Catches any exceptions and does not throw them. This is to preserve flow in the caller's finally block.
	 */
	public void closeSession() {
		try {
			ThreadSession ts = sessionManager.getExistingThreadSession();
			if (ts == null) {
				RuntimeException dre = new RuntimeException("Closing a session without a ThreadSession!");
				log.error(dre.getMessage(), dre);
				return; // nothing to do
			}

			ts.decrementSessionOpenCount();
			if (ts.getSessionOpenCount() <= 0) {
				Session s = ts.getSession();
				if (s != null && s.isOpen()) {
					sessionManager.closeThreadSession();
				}
			}
		} catch (Exception t) {
			log.warn("Unexpected exception while closing a ThreadSession");
		}
	}

	/**
	 * Close the session regardless of the current reference count.
	 * <p>
	 * This should *ONLY* be used at system exit points. Overriding the close of an open session is considered an error
	 * state.
	 */
	public void closeSessionFinal() {
		closeSession();

		// is it really closed?
		ThreadSession ts = sessionManager.getExistingThreadSession();
		if (ts != null) {
			log.warn("** Detected a session that was not properly closed! **", new Exception("stack, exception not thrown"));
			sessionManager.closeThreadSession();
		}
	}

	/**
	 * Flush the session, sending any sql to the database if the session isDirty().
	 *
	 * @throws HibernateException
	 */
	public void flushSession() throws HibernateException {
		ThreadSession ts = sessionManager.getExistingThreadSession();
		if (ts == null) { // uh oh.. did someone make a coding mistake?
			RuntimeException re = new RuntimeException("Flushing a session without a ThreadSession!");
			log.error(re.getMessage(), re);
			return; // nothing to do
		}

		Session s = ts.getSession();
		if (s != null && s.isDirty()) {
			s.flush();
		}
	}

	/**
	 * Begin a transaction
	 *
	 * @return Session
	 * @throws HibernateException
	 */
	public Session beginTransaction() throws HibernateException {
		// get a fresh session for this thread (if necessary)
		// this step is VITAL! reference counting is handled within the open!
		openSession();
		ThreadSession ts = sessionManager.getExistingThreadSession(); // session already created
		Persistence.openTransaction(ts);

		return ts.getSession();
	}

	/**
	 * Helper to consistently handle how transactions are opened. Both methods of beginning a tx use this.
	 *
	 * @param threadSession
	 */
	private static void openTransaction(ThreadSession threadSession) {
		Transaction t = threadSession.getTransaction();
		if (t == null) {
			threadSession.setTransaction(threadSession.getSession().beginTransaction());
		}
		threadSession.incrementTransactionOpenCount(); // record that we opened a transaction
	}

	/**
	 * End the transaction IF the caller is the one that opened it.
	 *
	 * @return true if and only if the transaction is the parent transaction
	 */
	public boolean endTransaction() {
		boolean parent = false;
		try {
			ThreadSession ts = sessionManager.getExistingThreadSession();
			if (ts == null) {
				log.error("Ending a transaction without a ThreadSession!", new RuntimeException());
				return false;
			}

			// if the caller owns the transaction then we will automatically rollback if they haven't committed/rolled back on
			// their own.
			if (Persistence.isCallerTransactionOwner(ts)) {
				if (!ts.getCommitted() && !ts.getRolledBack()) {
					log.warn("Rolling back uncommitted transaction");
					rollback();
				}
			}

			// ALWAYS decrement the count. if the transaction is finished then we can clean it up. it's not possible to reach
			// this point without having committed or rolling back, so we don't have to bother checking for those states.
			ts.decrementTransactionOpenCount();
			if (Persistence.isCallerTransactionFinished(ts)) {
				ts.setTransaction(null); // wipe out this transaction so a new one may be created
				parent = true;
			}

		} catch (Exception t) {
			RuntimeException dre = new RuntimeException("Unexpected exception while ending a transaction.", t);
			log.warn(dre.getMessage(), dre);
		} finally {
			closeSession(); // close macro will handle the session cleanup
		}

		return parent;
	}

	/**
	 * End the transaction regardless of the reference count.
	 * <p>
	 * This should *ONLY* be used at system exit points. Overriding the end of an open transaction is considered an error
	 * state.
	 *
	 * @return
	 */
	public boolean endTransactionFinal() {
		endTransaction();

		// is it really ended?
		final ThreadSession ts = sessionManager.getExistingThreadSession();
		if (ts != null) {
			final Transaction transaction = ts.getTransaction();
			if (transaction != null) {
				// shut the transaction down
				log.warn("** Detected a transaction that was not properly ended! **", new Exception(
						"stack, exception not thrown"));
				if (!ts.getCommitted() && !ts.getRolledBack()) {
					log.warn("Automatically rolling back a forcefully ended transaction.");
					transaction.rollback();
					ts.setTransaction(null); // wipe out this transaction so a new one may be created
				}
			}
		}

		return true;
	}

	/**
	 * Once a transaction is opened the reference count starts at 1 and each successive beginTransaction increases the
	 * count. Being the owner of the transaction means you started it and the reference count is at 1 or less.
	 *
	 * @param ts
	 * @return
	 */
	private static boolean isCallerTransactionOwner(ThreadSession ts) {
		boolean owner = false;
		if (ts.getTransactionOpenCount() <= 1) {
			owner = true;
		}

		return owner;
	}

	/**
	 * Once the owner of the transaction decrements the reference count, the count will fall to 0. A count of 0 or less
	 * means the owner has decremented the count and the transaction is permanently finished.
	 *
	 * @param ts
	 * @return
	 */
	private static boolean isCallerTransactionFinished(ThreadSession ts) {
		boolean finished = false;
		if (ts.getTransactionOpenCount() <= 0) {
			finished = true;
		}

		return finished;
	}

	/**
	 * Commit the transaction IF the caller is the one that opened it.
	 *
	 * @return true if and only if the transaction is the parent transaction
	 * @throws HibernateException
	 */
	public boolean commit() throws HibernateException {
		boolean parent = false;
		ThreadSession ts = sessionManager.getExistingThreadSession();
		if (ts == null) {
			log.error("Committing a transaction without a ThreadSession!", new RuntimeException());
			return parent;
		}
		Transaction transaction = ts.getTransaction();
		if (transaction == null) {
			log.error("Committing a transaction without a Transaction!", new RuntimeException());
			return parent;
		}

		// commit occurs before the close, so the caller that opened the transaction will be marked
		// with a 1, not a 0
		if (Persistence.isCallerTransactionOwner(ts)) {
			Session sess = ts.getSession();
			sess.flush();
			transaction.commit();
			parent = true;
			ts.setCommitted(true);
		} else {
			flushSession();
		}

		return parent;
	}

	/**
	 * Rollback the transaction IF the caller is the one that opened it. Well-behaved methods are expected to throw their
	 * exceptions to the parent, so the parent can decide what to do about the transaction it is responsible for.
	 *
	 * @return true if and only if the transaction is the parent transaction
	 */
	public boolean rollback() {
		ThreadSession ts = sessionManager.getExistingThreadSession();
		if (ts == null) {
			log.error("Rolling back a transaction without a ThreadSession!", new RuntimeException());
			return false;
		}
		Transaction transaction = ts.getTransaction();
		if (transaction == null) {
			log.error("Rolling back a transaction without a Transaction!", new RuntimeException());
			return false;
		}

		boolean parent = false;
		try {
			// rollback occurs before the close, so the user that opened the transaction will be marked
			// with a 1, not a 0
			if (Persistence.isCallerTransactionOwner(ts)) {
				transaction.rollback();
				parent = true;
				ts.setRolledBack(true);
			}
		} catch (Exception e) {
			// do not throw exceptions. something has already gone wrong to cause our caller to call rollback
			// at all. this is a best-effort to clean up and they're not interested if we fail.
			log.warn("Failed to rollback transaction!", e);
		}

		return parent;
	}

	/**
	 * From what I understand this should only be called after a commit or rollback.
	 */
	public void clearSession() {
		ThreadSession ts = sessionManager.getExistingThreadSession();
		if (ts == null) {
			log.error("Clearing session that without a ThreadSession!", new RuntimeException());
			return;
		}
		ts.getSession().clear();
	}

	/**
	 * Does a session currently exist?
	 */
	public boolean hasSession() {
		final ThreadSession ts = sessionManager.getExistingThreadSession();
		return ts != null;
	}

	// /**
	// * No Session? Throw an exception.
	// */
	// public void ensureSession() {
	// final ThreadSession ts = sessionManager.getExistingThreadSession();
	// if (ts == null) {
	// throw new SessionNotPresent("Session does not exist");
	// }
	// }
	//
	// /**
	// * Ensure that no session exists
	// */
	// public void ensureNoSession() {
	// final ThreadSession ts = sessionManager.getExistingThreadSession();
	// if (ts != null) {
	// throw new SessionPresentException("Session exists");
	// }
	// }
	//
	// /**
	// * No Transaction? Throw an exception.
	// */
	// public void ensureTransaction() {
	// final ThreadSession ts = sessionManager.getExistingThreadSession();
	// if (ts == null) {
	// throw new SessionNotPresent("Session is not open when checking for a transaction");
	// }
	// final Transaction t = ts.getTransaction();
	// if (t == null) {
	// throw new TransactionNotPresent("Transaction does not exist");
	// }
	// }
	//
	// /**
	// * Ensure that no transaction has been started for the current session, if a session exists at all. If a transaction
	// * has been started then throw a TransactionPresentException (Runtime) to alert the caller.
	// *
	// * This method should ONLY be placed in code that MAY NOT run in a database transaction. It is not meant to cause
	// * problems in production, but rather facilitate development and testing in order to make sure mistakes are not
	// made.
	// */
	// public void ensureNoTransaction() {
	// if (isTransaction()) {
	// throw new TransactionPresentException("Transaction exists");
	// }
	// }

	/**
	 * @return true if there is a transaction in effect
	 */
	public boolean isTransaction() {
		final ThreadSession ts = sessionManager.getExistingThreadSession();
		if (ts != null) {
			final Transaction t = ts.getTransaction();
			if (t != null && !t.getStatus().equals(TransactionStatus.COMMITTED)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Sets this session to no cache and to only flush manually. Use this for performance reasons.
	 */
	public void manual() {
		final ThreadSession ts = sessionManager.getExistingThreadSession();
		if (ts == null) {
			log.warn("No thread session present. You must have a session to declare manual.", new Exception(
					"stack, exception not thrown"));
			return;
		}

		// no need to catch exceptions- these commands should work and we're presumably already in a try/catch if something
		// is horribly wrong
		ts.setManual(true);
		final Session s = ts.getSession();
		s.setCacheMode(CacheMode.IGNORE);
		s.setFlushMode(FlushMode.MANUAL);
	}

	/**
	 * Check for any/all attributes set by the 'manual' mode. Have they been placed?
	 */
	private boolean isManual() {
		final ThreadSession ts = sessionManager.getExistingThreadSession();
		final boolean manual = ts != null && ts.isManual();
		return manual;
	}

	/**
	 * Use this to restart your session when processing a large number of items. You can use this in conjunction with
	 * 'manual' to process large groups of items efficiently. The session will be reopened when the count is reached.
	 *
	 * @param numItemsPerBatch
	 * @return true if the session was restarted
	 */
	public boolean restartSession(int numItemsPerBatch) {
		final ThreadSession ts = sessionManager.getExistingThreadSession();
		if (ts == null) {
			log.error("Incrementing batch count without a ThreadSession!", new RuntimeException());
			return false;
		}

		int count = ts.incrementSessionBatchCount();
		if (count >= numItemsPerBatch) {

			final boolean manualSession = isManual();
			closeSession();
			ts.resetSessionBatchCount();

			openSession();
			if (manualSession) {
				manual();
			}
			return true;
		}

		return false;
	}

	/**
	 * Use this to commit and restart your tx when processing a large number of items. You can use this in conjunction
	 * with 'manual' to process large groups of items efficiently. The tx will be restarted when the count is reached.
	 *
	 * @param numItemsPerBatch
	 * @return true if the tx was committed
	 */
	public boolean restartTransaction(int numItemsPerBatch) {
		ThreadSession ts = sessionManager.getExistingThreadSession();
		if (ts == null) {
			log.error("Incrementing batch count without a ThreadSession!", new RuntimeException());
			return false;
		}

		int count = ts.incrementBatchCount();
		if (count >= (numItemsPerBatch)) {

			final boolean manualSession = isManual();
			commit();
			endTransaction();

			ts.resetBatchCount();
			final Session s = ts.getSession();
			if (s.isOpen()) {
				s.clear();
			}

			beginTransaction();
			if (manualSession) {
				manual();
			}
			return true;
		}

		return false;
	}

	public Integer getSessionCount() {
		ThreadSession ts = sessionManager.getExistingThreadSession();
		return (ts != null) ? ts.getSessionOpenCount() : null;
	}
}
