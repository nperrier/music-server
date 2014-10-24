/* Create album table */

create table album (
  id bigint not null auto_increment,
  name varchar(255) not null,
  year timestamp,
  artist_id bigint,
  genre_id bigint,
  cover_art varchar(255),
  creation_date timestamp not null,
  modification_date timestamp not null,
  primary key(id),
  foreign key(artist_id) references artist (id),
  foreign key(genre_id) references genre (id),
  unique(name, artist_id)
);
