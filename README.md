#Music Server

A simple web app to stream music built using modern Java libraries and AngularJS

##Setup

###Dependencies

* Install Java 6
* Install Groovy 
* Install Node
* Install Node Package Manager
* Install Grunt

###Steps

* Run `npm install` in web directory
* Run `bower install` in web directory
* Run `gradle build` in root directory
* Run `gradle run` to start the server
* Run `grunt serve` in /web direcctory
* In browser, open http://localhost:8080

##TODO
* Add persisted history to record when a track was played
* Add UI to add full album to player queue
* Add a settings/config page for configuring various things
* Integration with other media player accounts:
  * SoundCloud
  * GrooveShark
  * Spotify
* Download albums
* Edit meta data of tracks/albums and remember to override when re-scanning (unless some kind of "force tag" option is set)
* Smart meta-info from source (e.g., musicbrainz)
* Implement search
