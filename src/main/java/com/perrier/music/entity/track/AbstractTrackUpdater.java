package com.perrier.music.entity.track;

import com.google.inject.Inject;
import com.perrier.music.db.DBException;
import com.perrier.music.db.IDatabase;

abstract class AbstractTrackUpdater<T> {

	protected final Track track;

	protected IDatabase db;

	AbstractTrackUpdater(Track track) {
		this.track = track;
	}

	@Inject
	public void setDatabase(IDatabase db) {
		this.db = db;
	}

	static class UpdateResult<E> {

		private final E update;
		private final Boolean changed;

		public UpdateResult(E update, Boolean changed) {
			this.update = update;
			this.changed = changed;
		}

		public E getUpdate() {
			return update;
		}

		public Boolean getChanged() {
			return changed;
		}

		@Override
		public String toString() {
			return "UpdateResult[" + "update=" + update + ", changed=" + changed + ']';
		}
	}

	abstract UpdateResult<T> handleUpdate(String trackChange) throws DBException;
}
