package com.perrier.music.db;

import org.hibernate.Session;

public abstract class UpdateQuery<T> extends HibernateQuery {

	public abstract T query(Session session) throws DBException;

}