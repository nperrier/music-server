/* Create genre table */

create table genre (
  id bigint not null auto_increment,
  name varchar(255) not null,
  creation_date timestamp not null,
  modification_date timestamp not null,
  primary key(id),
  unique(name)
);