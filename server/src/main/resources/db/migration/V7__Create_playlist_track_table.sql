/* Create playlist tracks table */

create table playlist_track (
  id bigint not null auto_increment,
  playlist_id bigint not null,
  track_id bigint not null,
  position int, /* not null */
  primary key (id),
  foreign key(playlist_id) references playlist (id),
  foreign key(track_id) references track (id)
  /* , unique(playlist_id, track_id, position) */
);
