'use strict';

/**
 * @ngdoc function
 * @name musicApp.directive:audioPlayerControls
 * @description
 * # AudioPlayerControls
 *
 */
angular.module('musicApp').directive('audioPlayerControls', [
  '$log',
  '$rootScope',
  '$interval',
  'AudioPlayer',
  'PlayerQueue',
  function(
    $log,
    $rootScope,
    $interval,
    AudioPlayer,
    PlayerQueue
  ) {

    return {
      restrict: 'E',
      scope: {}, // isolate scope
      templateUrl: '/views/audioPlayerControls.html',
      controller: function($scope) {

        $scope.currentTime = 0;
        $scope.currentPercentage = 0;
        // TODO: Better way to do this?
        $scope.model = AudioPlayer;
        $scope.queue = PlayerQueue;

        var timeUpdater = null;

        /* For some reason angular won't allow ng-model='audio.volume' anymore */
        $scope.audioVolume = {
          volume: function(val) {
            if (val) {
              AudioPlayer.setVolume(val);
            }
            return AudioPlayer.getVolume();
          }
        };

        // Tell audio player to play/pause
        $scope.playOrPause = function() {
          if (!AudioPlayer.isPlaying()) {
            AudioPlayer.play();
            startTimeUpdater();
          }
          else {
            stopTimeUpdater();
            AudioPlayer.pause();
          }
        };

        $scope.previous = function() {
          var track = PlayerQueue.getPrevious();
          if (track) {
            AudioPlayer.playNow(track);
          }
          else {
            resetPlayer();
          }
        };

        // try to play the next track in the queue
        $scope.next = function() {
          var track = PlayerQueue.getNext();
          if (track) {
            AudioPlayer.playNow(track);
          }
          else {
            resetPlayer();
          }
        };

        var startTimeUpdater = function() {
          if (!timeUpdater) {
            // update display of things every .5 seconds
            // makes the time-scrubber work
            timeUpdater = $interval(function() {
              if (AudioPlayer.isTrackLoaded()) {
                $scope.currentTime = AudioPlayer.getTrackTime();
                var percentPlayed = $scope.currentTime / (AudioPlayer.getTrackLength() / 1000) * 100;
                $scope.currentPercentage = Math.round(percentPlayed);
              }
            }, 500);
          }
        };

        var stopTimeUpdater = function() {
          if (timeUpdater) {
            $interval.cancel(timeUpdater);
            timeUpdater = null;
          }
        };

        var resetPlayer = function() {
          stopTimeUpdater();
          AudioPlayer.resetPlayer();
        };

        var audioPlay = $rootScope.$on('audio.play', function() {
          $log.debug('Event: audio.play');
          startTimeUpdater();
        });

        var audioPause = $rootScope.$on('audio.pause', function() {
          $log.debug('Event: audio.pause');
          stopTimeUpdater();
        });

        var audioEnd = $rootScope.$on('audio.ended', function() {
          $log.debug('Event: audio.ended');
          // play next track
          var nextTrack = PlayerQueue.getNext();
          if (nextTrack) {
            AudioPlayer.playNow(nextTrack);
          }
          else {
            resetPlayer();
          }
        });

        var queueTrackAdded = $rootScope.$on('queue.track.added', function(evt, track) {
          $log.debug('Event: queue.track.added, track: ' + track);
          // If there is no track loaded, play it:
          if (!AudioPlayer.isTrackLoaded()) {
            var nextTrack = PlayerQueue.getNext();
            AudioPlayer.playNow(nextTrack);
            startTimeUpdater();
          }
        });

        var queueTrackRemoved = $rootScope.$on('queue.tracks.removed', function(evt, tracks) {
          $log.debug('Event: queue.track.removed, tracks: ' + tracks);
          for (var i = 0; i < tracks.length; i++) {
            var t = tracks[i];
            if (AudioPlayer.isTrackLoaded(t)) {
              var nextTrack = PlayerQueue.getNext();
              if (nextTrack) {
                AudioPlayer.playNow(nextTrack);
              }
              else {
                resetPlayer();
                break;
              }
            }
          }
        });

        // The time slider is being moved. Don't update the knob position
        $scope.$on('slider.dragging', function() {
          $log.debug('Event: slider.dragging');
          stopTimeUpdater();
        });

        // The time slider is being moved. Update the current time
        $scope.$on('slider.moved', function(evt, seekPercentage) {
          $log.debug('Event: slider.moved: ' + seekPercentage + '%');
          if (seekPercentage < 0 || seekPercentage > 100) {
            throw new Error('Invalid seek percentage');
          }
          var duration = AudioPlayer.getDuration() / 1000; // to seconds
          var seekTime = Math.floor(((seekPercentage / 100) * duration));
          // TODO: may need handle edge cases (0 or 100)
          $scope.currentTime = seekTime;
        });

        // The slider knob has changed to a new position.  Seek the audio to the new position
        $scope.$on('slider.dropped', function(evt, seekPercentage) {
          $log.debug('Event: slider.dropped, new percentage value: ' + seekPercentage);
          AudioPlayer.seek(seekPercentage);
          startTimeUpdater();
        });

        // The slider knob has changed to a new position.  Seek the audio to the new position
        $scope.$on('slider.clicked', function(evt, seekPercentage) {
          $log.debug('Event: slider.clicked, new percentage value: ' + seekPercentage);
          AudioPlayer.seek(seekPercentage);
        });

        $scope.$on('$destroy', function() {
          stopTimeUpdater();
          // unbind listeners from $rootScope events:
          audioPlay();
          audioPause();
          audioEnd();
          queueTrackAdded();
          queueTrackRemoved();
        });
      }
    };
  }
]);
