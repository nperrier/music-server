/* Create playlist tracks table */

create table playlist_track (
  id serial not null,
  playlist_id bigint not null,
  track_id bigint not null,
  position int,
  primary key (id),
  foreign key(playlist_id) references playlist (id),
  foreign key(track_id) references track (id)
);
