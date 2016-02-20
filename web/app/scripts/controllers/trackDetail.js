'use strict';

/**
 * @ngdoc function
 * @name musicApp.controller:TrackDetailCtrl
 * @description
 * # TrackDetailCtrl
 * Controller of the musicApp
 */
angular.module('musicApp').controller('TrackDetailCtrl', [
  '$scope', '$routeParams', 'Track', function($scope, $routeParams, Track) {

  	Track.get({ trackId: $routeParams.trackId }, function(track) {
  		$scope.track = track;
  	});
  }
]);
