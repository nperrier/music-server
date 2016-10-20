/*
 * (c) Copyright 2016 Calabrio, Inc.
 * All Rights Reserved. www.calabrio.com LICENSED MATERIALS
 * Property of Calabrio, Inc., Minnesota, USA
 *
 * No part of this publication may be reproduced, stored or transmitted,
 * in any form or by any means (electronic, mechanical, photocopying,
 * recording or otherwise) without prior written permission from Calabrio, Inc.
 */

package com.perrier.music.entity.update;

class UpdateResult<E> {

	public enum Change {
		NONE, DELETED, UPDATED, CREATED
	}

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
		return !Change.NONE.equals(change);
	}

	public boolean isCreated() {
		return Change.CREATED.equals(change);
	}

	public boolean isDeleted() {
		return Change.DELETED.equals(change);
	}

	public boolean isCreatedOrDeleted() {
		return (Change.DELETED.equals(change) || Change.CREATED.equals(change));
	}

	@Override
	public String toString() {
		return "UpdateResult[" + "original=" + original + ", update=" + update + ", change=" + change + ']';
	}
}
