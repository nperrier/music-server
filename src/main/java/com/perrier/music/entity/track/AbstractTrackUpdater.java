package com.perrier.music.entity.track;

import static org.apache.commons.lang3.StringUtils.trimToEmpty;

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

		public enum Change {
			NONE, DELETED, UPDATED, CREATED
		};

		private final E original; // this is the value of the original object
		private final E update; // this is the new object value
		private final Change change; // this is the type of change

		public UpdateResult(E original, E update, Change change) {
			this.original = original;
			this.update = update;
			this.change = change;
		}

		public E getOriginal() {
			return original;
		}

		public E getUpdate() {
			return update;
		}

		public Change getChange() {
			return change;
		}

		public boolean isChanged() {
			return Change.NONE.equals(change);
		}

		public boolean isCreatedOrDeleted() {
			return (Change.DELETED.equals(change) || Change.CREATED.equals(change));
		}

		@Override
		public String toString() {
			return "UpdateResult[" + "original=" + original + ", update=" + update + ", change=" + change + ']';
		}
	}

	abstract UpdateResult<T> handleUpdate(String trackChange) throws DBException;

	protected String normalizeInput(String input) {
		String normalizedInput = trimToEmpty(input);
		normalizedInput = normalizedInput.replaceAll("\\s+", " "); // collapse an extra spaces

		return normalizedInput;
	}
}
