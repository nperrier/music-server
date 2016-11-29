package com.perrier.music.db;

import org.hibernate.Session;

/**
 * Create or update an object that may exist. Create if it doesn't already exist otherwise update.
 */
public abstract class ForceUpdateQuery<T> extends HibernateQuery {

	public abstract T query(Session session) throws DBException;

}