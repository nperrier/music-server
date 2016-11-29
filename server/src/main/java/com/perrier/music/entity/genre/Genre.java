package com.perrier.music.entity.genre;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.perrier.music.entity.AbstractAuditableEntity;

@Entity
@Table(name = "genre")
public class Genre extends AbstractAuditableEntity {

	private Long id;
	private String name;

	public Genre() {
	}

	public static Genre copy(Genre g) {
		if (g == null) {
			return null;
		}

		Genre genre = new Genre();
		genre.setId(g.getId());
		genre.setName(g.getName());
		genre.setCreationDate(g.getCreationDate());
		genre.setModificationDate(g.getModificationDate());

		return genre;
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
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Genre [id=" + id + ", name=" + name + "]";
	}
}
