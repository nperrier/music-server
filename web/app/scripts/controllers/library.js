'use strict';

/**
 * @ngdoc function
 * @name musicApp.controller:LibraryCtrl
 * @description
 * # LibraryCtrl
 * Controller of the musicApp
 */
angular.module('musicApp')
  .controller('LibraryCtrl', ['$scope', 'Library', function($scope, Library) {

	$scope.libraries = Library.query();

	$scope.sortField = 'path';

	$scope.createLibrary = function(library) {

		Library.save(library);
	};
}]);