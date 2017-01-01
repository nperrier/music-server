/* Add full-text search columns to all table columns to search */
alter table artist add column search_name tsvector;
create index artist_search_name_tsv_idx ON artist USING gin(search_name);

/* Build indexes on all rows in the table (this may take awhile) */
update artist set search_name = to_tsvector('pg_catalog.english', name);

/* Create db trigger that will update the search index when the name changes */
create trigger artist_search_name_update_trigger
  before insert or update on artist
  for each row execute procedure
  tsvector_update_trigger(search_name, 'pg_catalog.english', name);

/* Add full-text search columns to all table columns to search */
alter table album add column search_name tsvector;
create index album_search_name_tsv_idx ON album USING gin(search_name);

/* Build indexes on all rows in the table (this may take awhile) */
update album set search_name = to_tsvector('pg_catalog.english', name);

/* Create db trigger that will update the search index when the name changes */
create trigger album_search_name_update_trigger
  before insert or update on album
  for each row execute procedure
  tsvector_update_trigger(search_name, 'pg_catalog.english', name);

/* Add full-text search columns to all table columns to search */
alter table track add column search_name tsvector;
create index track_search_name_tsv_idx ON track USING gin(search_name);

/* Build indexes on all rows in the table (this may take awhile) */
update track set search_name = to_tsvector('pg_catalog.english', name);

/* Create db trigger that will update the search index when the name changes */
create trigger track_search_name_update_trigger
  before insert or update on track
  for each row execute procedure
  tsvector_update_trigger(search_name, 'pg_catalog.english', name);