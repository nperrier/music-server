'use strict';

/**
 * @ngdoc function
 * @name musicApp.controller:ArtistDetailCtrl
 * @description
 * # ArtistDetailCtrl
 * Controller of the musicApp
 */
angular.module('musicApp')
  .controller('ArtistDetailCtrl', ['$scope', '$routeParams', 'Artist', 'ArtistAlbum', function($scope, $routeParams, Artist, ArtistAlbum) {
	// Load artist from rest resource
	Artist.get({ artistId: $routeParams.artistId }, function(artist) {
		$scope.artist = artist;
	});

	// Load albums from rest resource
	ArtistAlbum.get({ artistId: $routeParams.artistId }, function(albums) {
		$scope.albums = albums;
	});

	$scope.sortField = 'name';
	$scope.reverse = true;
}]);
