package com.perrier.music.db;


public abstract class HibernateQuery {

	protected IDatabase db;

	protected void setDb(IDatabase db) {
		this.db = db;
	}

}
