'use strict';

/**
 * @ngdoc function
 * @name musicApp.controller:TracksCtrl
 * @description
 * # TracksCtrl
 * Controller of the musicApp
 */
angular.module('musicApp')
  .controller('TracksCtrl', ['$scope', '$log', 'Track', function($scope, $log, Track) {

	$scope.tracks = Track.query();

	$scope.sortField = 'name';
	$scope.reverse = true;

	// Add a track to the player queue:
	$scope.addToQueue = function(trackId) {
		$log.info('Add track to player queue, id: ' + trackId);
	};

}]);
