#Music Server

A simple web app to stream music built using modern java libraries and angularjs

##TODO
* add a Player Queue (persisted) model to keep track of all songs queued up to play
* add a History model to keep track of what was played and when
* add a Playlist model to group favorite tracks together for mass addition to the Player Queue
* add a Settings page for configuring various things
* integration with other media player accounts (Soundcloud)
* download tracks or albums
* edit meta data of tracks/albums and remember to override when re-scanning (unless some kind of "force tag" option is set)
* change concept of "Tag" to "MetaData" of track (since track might eventually come from source other than mp3 file such as soundcloud api)
* implement search (across all types)
