package com.perrier.music.db;

import org.hibernate.Session;

import com.google.common.util.concurrent.Service;

public interface IDatabase extends Service {

	<T> T find(FindQuery<T> query) throws DBException;

	<T> T create(CreateQuery<T> query) throws DBException;

	<T> T update(UpdateQuery<T> query) throws DBException;

	<T> T forceUpdate(ForceUpdateQuery<T> query) throws DBException;

	void delete(DeleteQuery query) throws DBException;

	Session beginTransaction() throws DBException;

	boolean endTransaction();

	Session openSession();

	void closeSession();

	boolean commit() throws DBException;

	boolean rollback();
}
