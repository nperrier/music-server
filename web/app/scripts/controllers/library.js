'use strict';

/**
 * @ngdoc function
 * @name musicApp.controller:LibraryCtrl
 * @description
 * # LibraryCtrl
 * Controller of the musicApp
 */
angular.module('musicApp')
  .controller('LibraryCtrl', ['$scope', '$log', '$modal', '$routeParams', 'Library',
    function($scope, $log, $modal, $routeParams, Library) {

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

      $scope.removeLibrary = function() {
        // TODO
      };

      $scope.createLibraryDialog = function() {

        var modalInstance = $modal.open({
          templateUrl: 'views/librarycreatemodal.html',
          backdrop: false,
          resolve: {},
          controller: function ($scope, $modalInstance) {

            $scope.save = function (library) {
              // TODO: Need to add client-side validation
              $modalInstance.close(library);
            };

            $scope.cancel = function () {
              $modalInstance.dismiss('cancelled');
            };
          }
        });

        modalInstance.result.then(
          function (library) {
            $scope.createLibrary(library);
          },
          function (reason) {
            $log.info('Modal dismissed: ' + reason);
          }
        );
      };
    }
  ]);
