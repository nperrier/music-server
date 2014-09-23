'use strict';

/**
 * @ngdoc function
 * @name musicApp.controller:LibraryCtrl
 * @description
 * # LibraryCtrl
 * Controller of the musicApp
 */
angular.module('musicApp')
  .controller('LibraryCtrl', ['$scope', '$log', '$routeParams', 'Library', function($scope, $log, $routeParams, Library) {

	$scope.libraries = Library.query();
	$scope.sortField = 'path';

	$scope.createLibrary = function(library) {
		Library.save(library).$promise.then(function () {
			// refresh the model so the UI updates after creating a new library
		  $scope.libraries = Library.query();
	  });
	};

	$scope.scanLibrary = function(library) {
		$log.info('Requesting scan for library: ' + library);

    Library.scan({ libraryId: library.id });
	};

	$scope.removeLibrary = function(library) {
		// TODO
	};

}]);
