/* Enable full-text search */

create alias if not exists FT_INIT for "org.h2.fulltext.FullText.init";
call FT_INIT();
