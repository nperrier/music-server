'use strict';

angular.module('musicApp')
.directive('audioPlayerControls', ['$log', '$rootScope', '$interval', 'PlayerQueue',
  function($log, $rootScope, $interval, PlayerQueue) {

    return {
      restrict: 'E',
      scope: {}, // isolate scope
      templateUrl: '/views/audioplayercontrols.html',
      controller: function($scope, $element) {

        $scope.audio = new Audio();
        $scope.currentTrack = null;
        $scope.currentTime = 0;
        $scope.currentPercentage = 0;

        /* For some reason angular won't allow ng-model='audio.volume' anymore */
        $scope.audioVolume = {
          volume: function(val) {
            if (val) {
              $scope.audio.volume = val;
            }
            return $scope.audio.volume;
          }
        };

        var timeUpdater = null;

        $scope.previous = function() {
          var track = PlayerQueue.getPrevious();

          if (track) {
            $scope.playNow(track);
          }
          else {
            resetPlayer();
          }
          //$rootScope.$emit('audio.prev');
        };

        var getDuration = function() {
          if ($scope.currentTrack) {
            return $scope.currentTrack.length;
          }
          return 0;
        };

        // try to play the next track in the queue
        $scope.next = function() {
          var track = PlayerQueue.getNext();

          if (track) {
            $scope.playNow(track);
          }
          else {
            resetPlayer();
          }
        };

        // TODO: Somehow "bind" my PlayerQueue "model"
        // to the views using angular
        $scope.hasNext = function() {
          return PlayerQueue.hasNext();
        };

        $scope.hasPrevious = function() {
          return PlayerQueue.hasPrevious();
        };

        // tell audio element to play/pause
        $scope.playOrPause = function() {
          if ($scope.audio.paused) {
            $scope.audio.play();
            startTimeUpdater();
          }
          else {
            stopTimeUpdater();
            $scope.audio.pause();
          }
        };

        var startTimeUpdater = function() {
          if (!timeUpdater) {
            // update display of things every .5 seconds
            // makes the time-scrubber work
            timeUpdater = $interval(function() {
              if ($scope.currentTrack) {
                $scope.currentTime = $scope.audio.currentTime;
                var percentPlayed = $scope.currentTime / ($scope.currentTrack.length / 1000) * 100;
                $scope.currentPercentage = Math.round(percentPlayed);
              }
            }, 500);
          }
        }

        var stopTimeUpdater = function() {
          if (timeUpdater) {
            $interval.cancel(timeUpdater);
            timeUpdater = null;
          }
        }

        // listen for audio-element events, and broadcast stuff
        $scope.audio.addEventListener('play', function() {
          $rootScope.$emit('audio.play', this);
        });

        $scope.audio.addEventListener('pause', function() {
          $rootScope.$emit('audio.pause', this);
        });

        $scope.audio.addEventListener('timeupdate', function() {
          $rootScope.$emit('audio.time', this);
        });

        $scope.audio.addEventListener('ended', function() {
          $rootScope.$emit('audio.ended', this);
          $scope.next();
        });

        // The time slider is being moved. Don't update the knob position
        $scope.$on('slider.dragging', function(evt) {
          console.log('Event: slider.dragging');
          stopTimeUpdater();
        });

        // The slider knob has changed to a new position.  Seek the audio to the new position
        $scope.$on('slider.dropped', function(evt, seekPercentage) {
          console.log('Event: slider.dropped, new percentage value: ' + seekPercentage);
          seek(seekPercentage);
          startTimeUpdater();
        });

        // seek to a new audio position
        var seek = function(seekPercentage) {
          if (!isTrackLoaded()) {
            return;
          }

          if (seekPercentage < 0 || seekPercentage > 100) {
            throw new Error('Invalid seek percentage');
          }

          var duration = getDuration() / 1000; // to seconds
          var seekTime = Math.floor(((seekPercentage / 100) * duration));
          // TODO: may need handle edge cases (0 or 100)
          console.log('Seeking to position: ' + seekTime);
          $scope.audio.currentTime = seekTime;
        };

        // play the given track immediately
        $scope.playNow = function(track) {
          resetPlayer();

          $scope.currentTrack = track;

          $scope.audio.src = track.streamUrl;
          $scope.audio.load();
          $scope.audio.play();

          startTimeUpdater();
        };

        var resetPlayer = function() {
          stopTimeUpdater();

          $scope.audio.pause();
          $scope.audio.removeAttribute('src');

          $scope.currentTrack = null;
          $scope.currentTime = 0;
          $scope.currentPercentage = 0;
        };

        // a track has been added to the queue
        // auto-play if the player has no track loaded
        $rootScope.$on('track.added', function(event) {
          if (!isTrackLoaded()) {
            var track = PlayerQueue.getNext();
            $scope.playNow(track);
          }
        });

        // a track has been removed from the queue
        // if current track is playing, stop it and reset
        // Right now we assume that this event is only fired if the current track was playing
        // because the PlayerQueue keeps track of this, not the audio player
        // TODO: could add an 'isPlaying' boolean to object send with event...
        $rootScope.$on('track.removed', function(event) {
          // play the next track in the queue, if there is one
          $scope.next();
        });

        // TODO: NOT USED
        $scope.isPlaying = function() {
          if (isTrackLoaded()) {
            return !$scope.audio.paused || !$scope.audio.ended;
          }
          return false;
        }

        // this should only return true if the player isn't playing
        // and the PlayerQueue is empty
        var isTrackLoaded = function() {
          // Not sure how to tell if Audio element has a track or not...
          // May need to use some sort of 'state' variable instead
          return $scope.audio.src;
        };

        // $element.on() ?
        $scope.$on('$destroy', function() {
          stopTimeUpdater();
        });

      }
    };
  }]);
