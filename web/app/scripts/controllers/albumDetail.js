'use strict';

/**
 * @ngdoc function
 * @name musicApp.controller:AlbumDetailCtrl
 * @description
 * # AlbumDetailCtrl
 * Controller of the musicApp
 */
angular.module('musicApp')
  .controller('AlbumDetailCtrl', ['$scope', '$routeParams', 'Album', 'AlbumTrack',
    function($scope, $routeParams, Album, AlbumTrack) {

	$scope.sortField = 'number';
	$scope.reverse = false;

	Album.get({ albumId: $routeParams.albumId }, function(album) {
		$scope.album = album;
	});

	AlbumTrack.get({ albumId: $routeParams.albumId }, function(tracks) {
		$scope.tracks = tracks;
	});

}]);
