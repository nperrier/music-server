/* Create playlist tracks table */

create table playlist_track (
  playlist_id bigint not null,
  track_id bigint not null,
  position int, /* not null */
  foreign key(playlist_id) references playlist (id),
  foreign key(track_id) references track (id),
  /* unique(playlist_id, track_id, position) */
);
