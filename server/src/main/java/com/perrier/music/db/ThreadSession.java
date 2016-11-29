package com.perrier.music.db;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * Simply stores the session and its associated transaction (if there is one). Nothing fancy.
 */
public class ThreadSession {

	private Session session = null;
	private Transaction transaction = null;
	private int sessionOpenCount = 0;
	private int transactionOpenCount = 0;
	private int sessionBatchCount = 0;
	private int txBatchCount = 0;
	private boolean committed = false;
	private boolean rolledBack = false;
	private boolean manual = false;
	private Map<String, Object> props = null;

	public ThreadSession(Session session) {
		this.session = session;
	}

	public Session getSession() {
		return this.session;
	}

	public boolean hasTransaction() {
		return this.transaction != null;
	}

	public Transaction getTransaction() {
		return this.transaction;
	}

	public void setTransaction(Transaction transaction) {
		this.transaction = transaction;
		if (this.transaction == null) {
			this.committed = false;
			this.rolledBack = false;
		}
	}

	/**
	 * @return Returns the accessCount.
	 */
	public int getSessionOpenCount() {
		return this.sessionOpenCount;
	}

	public boolean getCommitted() {
		return this.committed;
	}

	public void setCommitted(boolean committed) {
		this.committed = committed;
		this.resetBatchCount();
	}

	public boolean getRolledBack() {
		return this.rolledBack;
	}

	public void setRolledBack(boolean rolledBack) {
		this.rolledBack = rolledBack;
		this.resetBatchCount();
	}

	public void incrementSessionOpenCount() {
		this.sessionOpenCount++;
	}

	public void decrementSessionOpenCount() {
		this.sessionOpenCount--;
	}

	public int getTransactionOpenCount() {
		return this.transactionOpenCount;
	}

	public void incrementTransactionOpenCount() {
		this.transactionOpenCount++;
	}

	public void decrementTransactionOpenCount() {
		this.transactionOpenCount--;
	}

	public void setProperty(String key, Object value) {
		if (this.props == null) {
			this.props = new HashMap<String, Object>();
		}
		this.props.put(key, value);
	}

	public Object getProperty(String key) {
		if (this.props == null) {
			return null;
		}
		return this.props.get(key);
	}

	public Object getProperty(String key, Object defaultValue) {
		if (this.props == null) {
			return defaultValue;
		}
		Object o = this.props.get(key);
		if (o == null) {
			o = defaultValue;
		}
		return o;
	}

	public int getSessionBatchCount() {
		return this.sessionBatchCount;
	}

	public int incrementSessionBatchCount() {
		return ++this.sessionBatchCount;
	}

	public int resetSessionBatchCount() {
		this.sessionBatchCount = 0;
		return this.sessionBatchCount;
	}

	public int incrementBatchCount() {
		return ++this.txBatchCount;
	}

	public int getBatchCount() {
		return this.txBatchCount;
	}

	public int resetBatchCount() {
		this.txBatchCount = 0;
		return this.txBatchCount;
	}

	public boolean isManual() {
		return this.manual;
	}

	public void setManual(boolean manual) {
		this.manual = manual;
	}

	@Override
	public String toString() {
		return ThreadSession.class.getSimpleName() + " [session=" + this.session + ", transaction=" + this.transaction
				+ ", sessionOpenCount=" + this.sessionOpenCount + ", transactionOpenCount=" + this.transactionOpenCount
				+ ", props=" + this.props + ", committed=" + this.committed + ", rolledBack=" + this.rolledBack
				+ ", sessionBatchCount=" + this.sessionBatchCount + ", txBatchCount=" + this.txBatchCount + ", manual="
				+ this.manual + "]";
	}
}