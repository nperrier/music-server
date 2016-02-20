'use strict';

/**
 * @ngdoc function
 * @name musicApp.controller:GenresCtrl
 * @description
 * # GenresCtrl
 * Controller of the musicApp
 */
angular.module('musicApp').controller('GenresCtrl', [
  '$scope', '$timeout', 'usSpinnerService', 'Genre',
  function($scope, $timeout, usSpinnerService, Genre) {

    $scope.sortField = 'name';
    $scope.reverse = false;
    $scope.doneLoading = false;

    // wait 1.5 seconds before showing spinner
    $timeout(function () {
      if (!$scope.doneLoading) {
        usSpinnerService.spin('spinner-loading');
      }
    }, 1500);

	  $scope.genres = Genre.query(function () {
      usSpinnerService.stop('spinner-loading');
      $scope.doneLoading = true;
    });
  }
]);
