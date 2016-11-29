/* Create track table */

create table track (
  id bigint not null auto_increment,
  name varchar(255) not null,
  number int,
  year char(4),
  length bigint not null,
  artist_id bigint,
  album_artist_id bigint,
  album_id bigint,
  genre_id bigint,
  audio_hash varchar(255) not null,
  audio_storage_key varchar(255) not null,
  audio_url varchar(255) not null,
  cover_hash varchar(255),
  cover_storage_key varchar(255),
  cover_url varchar(255),
  edited boolean default false not null,
  file_modification_date timestamp not null,
  creation_date timestamp not null,
  modification_date timestamp not null,
  primary key(id),
  foreign key(artist_id) references artist (id),
  foreign key(album_id) references album (id),
  foreign key(album_artist_id) references artist (id),
  foreign key(genre_id) references genre (id),
  unique(name, artist_id, album_id)
);

call ft_create_index('PUBLIC', 'TRACK', 'NAME');
