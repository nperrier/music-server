'use strict';

/**
 * @ngdoc function
 * @name musicApp.controller:QueueCtrl
 * @description
 * # QueueCtrl
 * Controller of the musicApp
 */
angular.module('musicApp')
  .controller('QueueCtrl', ['$scope', '$log', '$rootScope', 'PlayerQueue',
    function ($scope, $log, $rootScope, PlayerQueue) {

    $scope.tracks = PlayerQueue.getTracks();
    $scope.paused = true;

    // Indicate the "currently playing" Track in the view
    $scope.isPlaying = function(trackIndex) {
      var currentTrackIndex = PlayerQueue.getCurrentIndex();
      // currentTrack can be null
      if (currentTrackIndex >= 0) {
        var isCurrent = (currentTrackIndex === trackIndex);
        return isCurrent && !$scope.paused;
      }
      return false;
    };

    $scope.remove = function(track, position) {
      $log.info('Removing track.id: ' + track.id + ' at position: ' + position);
      PlayerQueue.removeTrack(position);
    };

    $scope.clearQueue = function() {
      $log.info('Removing all tracks');
      PlayerQueue.clear();
      $scope.paused = true;
    };

    // TODO: This should update the view
    $rootScope.$on('track.added', function() {
      $log.info('track.added called');
    });

    // TODO: This should update the view
    $rootScope.$on('track.removed', function() {
      $log.info('track.removed called');
    });

    // sent by audio player when track begins playing
    $rootScope.$on('audio.play', function() {
      $log.info('audio.play called');
      $scope.paused = false;
    });

    // sent by audio player when track has ended
    $rootScope.$on('audio.ended', function() {
      $log.info('audio.ended called');
      $scope.paused = true;
    });

    // sent by audio player when track has paused
    $rootScope.$on('audio.pause', function() {
      $log.info('audio.pause called');
      $scope.paused = true;
    });

  }]);
