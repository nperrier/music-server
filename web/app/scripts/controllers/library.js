'use strict';

/**
 * @ngdoc function
 * @name musicApp.controller:LibraryCtrl
 * @description
 * # LibraryCtrl
 * Controller of the musicApp
 */
angular.module('musicApp')
  .controller('LibraryCtrl', ['$scope', '$log', '$routeParams', 'Library', 'LibraryScan', function($scope, $log, $routeParams, Library, LibraryScan) {

	$scope.libraries = Library.query();
	$scope.sortField = 'path';

	$scope.createLibrary = function(library) {

		Library.save(library);
	};

	$scope.scanLibrary = function(library) {
		$log.info('Requesting scan for library: ' + library);

    Library.scan({ libraryId: library.id });
		//LibraryScan.scan({ libraryId: library.id });
	};

}]);
