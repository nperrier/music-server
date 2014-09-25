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
    $scope.isPlaying = function(trackId) {
      var currentTrack = PlayerQueue.getCurrent();
      // currentTrack can be null
      if (currentTrack) {
        return currentTrack.id == trackId;
      }
      return false;
    };

    $scope.remove = function(track, position) {
      $log.info('remove track.id: ' + track.id + ' at position: ' + position);
      PlayerQueue.removeTrack(position);
    }

    // This should update the view
    $rootScope.$on('track.added', function() {
      $log.info('track.added called');
    });

    $rootScope.$on('track.removed', function() {
      $log.info('track.removed called');
    });
  }]);
