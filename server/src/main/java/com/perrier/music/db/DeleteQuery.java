package com.perrier.music.db;

import org.hibernate.Session;

public abstract class DeleteQuery extends HibernateQuery {

	public abstract void query(Session session) throws DBException;

}
