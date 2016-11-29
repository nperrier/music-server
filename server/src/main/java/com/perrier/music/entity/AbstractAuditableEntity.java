package com.perrier.music.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.perrier.music.db.AuditInterceptor;

/**
 * The dates will be set on entity save/update via {@link AuditInterceptor}
 *
 */
@MappedSuperclass
public class AbstractAuditableEntity {
	
	protected Date creationDate;
	protected Date modificationDate;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "creation_date", nullable = false)
	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "modification_date", nullable = false)
	public Date getModificationDate() {
		return modificationDate;
	}

	public void setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
	}

}
