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

    // Indicate the "currently playing" Track in the view
    $scope.isPlaying = function(trackIndex) {
      var currentTrackIndex = PlayerQueue.getCurrentIndex();
      // currentTrack can be null
      if (currentTrackIndex >= 0) {
        return currentTrackIndex === trackIndex;
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
    };

    // This should update the view
    $rootScope.$on('track.added', function() {
      $log.info('track.added called');
    });

    $rootScope.$on('track.removed', function() {
      $log.info('track.removed called');
    });
  }]);
