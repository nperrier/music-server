#TODO

* Add persisted history to record when a track was played
* Add a settings/config page for configuring various things
* Integration with other media player accounts:
  * SoundCloud
  * GrooveShark
  * Spotify
* Smart meta-info from source (e.g., musicbrainz)
* Add auto-completer suggestions for Edit input fields (i.e., suggest a genre)
* Implement handling of changed metadata for media files in indexer
* Ensure that artist/album/genre/song names are being queried case-insensitive
* TrackAlbumUpdater: Creating a new album should associate the cover art with the album
* Edit meta data of tracks/albums and remember to override when re-scanning (unless some kind of "force tag" option is set)
* Generate small and large cover art
* Add 000 migration with user and grants for db (so it can only read, not to privelged sql cmds)
* Malicious script could come from ID3 tags of mp3's (e.g., artist name or even image).  Ensure input is validated
* Ensure 'Edit track' input is validated
* Add auth header to img source url's so they don't have to be unauthenticated
* Find a way to serve audio files in an authenticated way.  Currently they include the auth token in the query param of the src url
  * Could use signed urls
* Multi-edit tracks
* Add track action menu to playlist track table
* Create installer and upgrade scripts
* Support multiple user accounts
* Download albums from s3 can take awhile before it starts since it's requesting a lot of tracks at once
  - Create a message to indicated this in the UI. Also investigate ways to speed up creating zip (could download tracks from s3 in parallel)
