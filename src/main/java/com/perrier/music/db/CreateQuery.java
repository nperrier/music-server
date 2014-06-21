package com.perrier.music.db;

import org.hibernate.Session;

public abstract class CreateQuery<T> extends HibernateQuery {

	public abstract T query(Session session) throws DBException;

}