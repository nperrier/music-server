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

    // Need a way to indicate the "currently playing" Track
    // in order to show a visual indicator in the queue view
    $scope.tracks = PlayerQueue.getTracks();

    // This should update the view
    $rootScope.$on('track.added', function() {
      $log.info('track.added called');
    });

    $rootScope.$on('track.removed', function() {
      $log.info('track.removed called');
    });
  }]);
