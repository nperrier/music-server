#Music Server

A simple web app to stream music built using modern Java libraries and AngularJS

##Setup

###Dependencies

* Install Java 6 SDK
* Install Groovy 
* Install Node
* Install Node Package Manager
* Install Grunt

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
* playing icon does not get removed after the last song in the queue ends
* 1969 is displayed for songs without year field
* Changing/Removing the associated album, genre, artist, etc. of an existing track should remove orphaned associates (i.e., if no tracks for an album, then delete the album)
* Add a 'Shuffle' feature
* Add a 'Play Now' feature (currently can only add songs to end of queue)
* Ensure that artist/album/genre/song names are being queried case-insensitive
* TrackAlbumUpdater: Creating a new album should associate the cover art with the album