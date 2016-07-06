#Music Server

A simple web app to stream music built using modern Java libraries and AngularJS

##Setup

###Dependencies

* Install Java 8 SDK
* Install Groovy
* Install Node
* Install Node Package Manager
* Install Grunt CLI

###Steps

* Run `npm install` in web directory
* Run `bower install` in web directory
* Run `gradle build` in root directory
* Run `gradle run` to start the server
* Run `grunt serve` in /web directory
* In browser, open http://localhost:9000

##TODO
* Add persisted history to record when a track was played
* Add a settings/config page for configuring various things
* Integration with other media player accounts:
  * SoundCloud
  * GrooveShark
  * Spotify
* Download albums
* Edit meta data of tracks/albums and remember to override when re-scanning (unless some kind of "force tag" option is set)
* Smart meta-info from source (e.g., musicbrainz)
* Implement search: (client-side or server-side?)
* Implement handling of changed metadata for media files in indexer
* Add ability to delete Library
* Add a 'Shuffle' feature
* Add a 'Play Now' feature (currently can only add songs to end of queue)
* Ensure that artist/album/genre/song names are being queried case-insensitive
* TrackAlbumUpdater: Creating a new album should associate the cover art with the album
* Show 'Scanning...' when library is currently scanning on Library page
* Add auto-completer suggestions for Edit input fields (i.e., suggest a genre)
* Use 'ETag' header to cache cover art
* Generate small and large cover art
* Add menu to first col of queue list items to play/pause
* Add 000 migration with user and grants for db (so it can only read, not to privelged sql cmds)
* Malicious script could come from ID3 tags of mp3's (e.g., artist name or even image).  Ensure input is validated
* Ensure 'Edit track' input is validated
* Redirect 401 to login page
* Add auth header to img source url's so they don't have to be unauthenticated
* Find a way to serve audio files in an authenticated way.  Currently they include the auth token in the query param of the src url

