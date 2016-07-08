
#Bugs

* FIX: track numbers to not default to 0 in UI?


##Issue 1

* Add track to queue (starts playing)
* Add another track to queue
* Skip to Next Track
* Go to Queue
* Remove currently playing track

###Expected

Previous track starts playing

###Actual

Queue has track but doesn't starting playing and controls are greyed out


##Issue 2

If a track is playing and we are on the 'all tracks' page, quickly scrolling the list (flinging scroll bar) causes the music to stutter.  There might be too many HTTP requests for the cover art happening at the same time which overloads the server CPU. Also could be the browswer stuttering.  Might need to throttle the requests to the cover art.

##Upgrading

replace /bin
replace /lib
replace /web
replace /resources

leave   /covers
leave   /db
leave   /log
leave?  /conf

