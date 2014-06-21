/* Create library table */

create table library (
  id bigint not null auto_increment,
  path varchar(255) not null,
  last_indexed_date timestamp,
  creation_date timestamp not null,
  modification_date timestamp not null,
  primary key(id),
  unique(path)
);