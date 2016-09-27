'use strict';

/**
 * @ngdoc function
 * @name musicApp.controller:TrackDetailCtrl
 * @description
 * # TrackDetailCtrl
 * Controller of the musicApp
 */
angular.module('musicApp').controller('TrackDetailCtrl', [
  '$scope',
  '$stateParams',
  'Track',
  function(
    $scope,
    $stateParams,
    Track
  ) {

  	Track.get({ trackId: $stateParams.id }, function(track) {
  		$scope.track = track;
  	});
  }
]);
