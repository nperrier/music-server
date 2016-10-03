package com.perrier.music.entity.library;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.perrier.music.entity.AbstractAuditableEntity;

@Entity
@Table(name = "library")
public class Library extends AbstractAuditableEntity {

	private Long id;
	private String path;
	private Date lastIndexedDate;

	public Library() {
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(nullable = false, unique = true, length = 255)
	public String getPath() {
		return this.path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "last_indexed_date", nullable = true)
	public Date getLastIndexedDate() {
		return lastIndexedDate;
	}

	public void setLastIndexedDate(Date lastIndexedDate) {
		this.lastIndexedDate = lastIndexedDate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Library))
			return false;
		Library other = (Library) obj;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Library{" +
				"id=" + id +
				", path='" + path + '\'' +
				", lastIndexedDate=" + lastIndexedDate +
				'}';
	}
}
