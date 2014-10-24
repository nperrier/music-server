/* Create track table */

create table track (
  id bigint not null auto_increment,
  name varchar(255) not null,
  number int,
  year timestamp,
  length bigint not null,
  path varchar(255) not null,
  artist_id bigint,
  album_id bigint,
  genre_id bigint,
  cover_art varchar(255),
  library_id bigint not null,
  file_modification_date timestamp not null,
  creation_date timestamp not null,
  modification_date timestamp not null,
  primary key(id),
  foreign key(artist_id) references artist (id),
  foreign key(album_id) references album (id),
  foreign key(genre_id) references genre (id),
  foreign key(library_id) references library (id),
  unique(name, artist_id, album_id)
);
