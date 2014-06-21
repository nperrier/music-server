package com.perrier.music.db;

import java.io.Serializable;
import java.util.Date;

import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;

import com.perrier.music.entity.AbstractAuditableEntity;

public class AuditInterceptor extends EmptyInterceptor {

	private static final long serialVersionUID = -2651853960061831806L;

	// Save
	@Override
	public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
		boolean stateModified = false;
		if (entity instanceof AbstractAuditableEntity) {
			Date now = new Date();
			for (int i = 0; i < propertyNames.length; i++) {
				if ("creationDate".equals(propertyNames[i])) {
					state[i] = now;
					stateModified = true;
				}
				if ("modificationDate".equals(propertyNames[i])) {
					state[i] = now;
					stateModified = true;
				}
			}
		}
		return stateModified;
	}

	// Update
	@Override
	public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) {

		if (entity instanceof AbstractAuditableEntity) {
			for (int i = 0; i < propertyNames.length; i++) {
				if ("modificationDate".equals(propertyNames[i])) {
					currentState[i] = new Date();
					return true;
				}
			}
		}
		return false;
	}
}
