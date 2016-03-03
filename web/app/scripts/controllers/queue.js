'use strict';

/**
 * @ngdoc function
 * @name musicApp.controller:QueueCtrl
 * @description
 * # QueueCtrl
 * Controller of the musicApp
 */
angular.module('musicApp')
  .controller('QueueCtrl', ['$scope', '$log', '$rootScope', '$timeout', 'PlayerQueue',
    function ($scope, $log, $rootScope, $timeout, PlayerQueue) {

    $scope.tracks = PlayerQueue.getTracks();
    $scope.doneLoading = false;

    // Fade the page in:
    $timeout(function() {
      $scope.doneLoading = true;
    }, 10);

    // Indicate the "currently playing" Track in the view
    $scope.isPlaying = function(trackIndex) {
      var currentTrackIndex = PlayerQueue.getCurrentIndex();
      // currentTrack can be null
      if (currentTrackIndex >= 0) {
        var isCurrent = (currentTrackIndex === trackIndex);
        return isCurrent;
      }
      return false;
    };

    $scope.remove = function(track, position) {
      $log.debug('Removing track.id: ' + track.id + ' at position: ' + position);
      PlayerQueue.removeTrack(position);
    };

    $scope.clearQueue = function() {
      $log.debug('Removing all tracks');
      PlayerQueue.clear();
    };

    $scope.onTrackMoveSort = function($item, $partFrom, $partTo, $indexFrom, $indexTo) {
      $log.debug('move sorted, fromIndex: ' + $indexFrom + ', toIndex: ' + $indexTo);
      PlayerQueue.moveTrack($indexFrom, $indexTo);
    };

    // TODO: This should update the view
    $rootScope.$on('track.added', function() {
      $log.debug('track.added called');
    });

    // TODO: This should update the view
    $rootScope.$on('track.removed', function() {
      $log.debug('track.removed called');
    });

    // sent by audio player when track begins playing
    $rootScope.$on('audio.play', function() {
      $log.debug('audio.play called');
    });

    // sent by audio player when track has ended
    $rootScope.$on('audio.ended', function() {
      $log.debug('audio.ended called');
    });

    // sent by audio player when track has paused
    $rootScope.$on('audio.pause', function() {
      $log.debug('audio.pause called');
    });
  }
]);
