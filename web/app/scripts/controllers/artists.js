'use strict';

/**
 * @ngdoc function
 * @name musicApp.controller:ArtistsCtrl
 * @description
 * # ArtistsCtrl
 * Controller of the musicApp
 */
angular.module('musicApp')
  .controller('ArtistsCtrl', ['$scope', 'Artist', function($scope, Artist) {

	$scope.artists = Artist.query();

	$scope.sortField = 'name';
	$scope.reverse = true;
}]);