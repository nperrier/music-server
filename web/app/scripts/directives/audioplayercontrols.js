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
        $scope.duration = 0;

        $scope.previous = function() {
          var track = PlayerQueue.getPrevious();

          if (track) {
            $scope.playNow(track);
          }
          else {
            $scope.resetPlayer();
          }
          //$rootScope.$emit('audio.prev');
        };

        $scope.getDuration = function() {
          return $scope.currentTrack.length;
        };

        // try to play the next track in the queue
        $scope.next = function() {
          var track = PlayerQueue.getNext();

          if (track) {
            $scope.playNow(track);
          }
          else {
            $scope.resetPlayer();
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
          }
          else {
            $scope.audio.pause();
          }
        };

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

        // play the given track immediately
        $scope.playNow = function(track) {
          $scope.audio.pause();
          $scope.audio.src = track.streamUrl;
          $scope.audio.load();
          $scope.audio.play();

          $scope.currentTrack = track;

          $scope.currentTime = 0;
          $scope.currentPercentage = 0;
          $scope.duration = $scope.currentTrack.length;
        };

        $scope.resetPlayer = function() {
          $scope.audio.pause();
          $scope.audio.removeAttribute('src');

          $scope.currentTrack = null;

          $scope.currentTime = 0;
          $scope.currentPercentage = 0;
          $scope.duration = 0;
        };

        // a track has been added to the queue
        // auto-play if the player has no track loaded
        $rootScope.$on('track.added', function(event) {
          if (!$scope.isTrackLoaded()) {
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
          $scope.resetPlayer();
          // play the next track in the queue, if there is one
          var track = PlayerQueue.getNext();
          if (track) {
            $scope.playNow(track);
          }
        });

        // this should only return true if the player isn't playing
        // and the PlayerQueue is empty
        $scope.isTrackLoaded = function() {
          // Not sure how to tell if Audio element has a track or not...
          // May need to use some sort of 'state' variable instead
          return $scope.audio.src;
        };

        // TODO: create this only when a track is playing and destroy when not playing
        // Also stop the updating if paused
        // update display of things every .5 seconds
        // makes the time-scrubber work
        // listen for events from 'timeupdate' on Audio() element?
        var timeUpdater = $interval(function() {
          if ($scope.currentTrack) {
            $scope.currentTime = $scope.audio.currentTime;
            var percentPlayed = $scope.currentTime / ($scope.currentTrack.length / 1000) * 100;
            $scope.currentPercentage = Math.round(percentPlayed);
          }
        }, 500);

      }
    };
  }]);
