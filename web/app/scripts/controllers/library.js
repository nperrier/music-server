'use strict';

/**
 * @ngdoc function
 * @name musicApp.controller:LibraryCtrl
 * @description
 * # LibraryCtrl
 * Controller of the musicApp
 */
angular.module('musicApp')
  .controller('LibraryCtrl', ['$scope', '$log', '$routeParams', 'Library',
    function($scope, $log, $routeParams, Library) {

      $scope.sortField = 'path';
      $scope.doneLoading = false;

      $scope.libraries = Library.query(function() {
        $scope.doneLoading = true;
      });

      $scope.createLibrary = function(library) {
        Library.save(library, function (l) {
          $scope.libraries.push(l);
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
