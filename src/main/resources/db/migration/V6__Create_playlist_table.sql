/* Create playlist table */

create table playlist (
  id bigint not null auto_increment (2, 1), /* start at 2, incr by 1's */
  name varchar(255) not null,
  creation_date timestamp not null,
  modification_date timestamp not null,
  primary key (id)
);

/* Create the player's playlist */
insert into playlist (id, name, creation_date, modification_date) values (1, 'DEFAULT', now(), now());