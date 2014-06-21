package com.perrier.music.db;

import org.hibernate.SessionFactory;

public interface IDBConfiguration {

	SessionFactory create() throws DBException;
}
