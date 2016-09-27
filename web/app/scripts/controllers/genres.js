'use strict';

/**
 * @ngdoc function
 * @name musicApp.controller:GenresCtrl
 * @description
 * # GenresCtrl
 * Controller of the musicApp
 */
angular.module('musicApp').controller('GenresCtrl', [
  '$scope',
  '$timeout',
  'LoadingSpinner',
  'Genre',
  function(
    $scope,
    $timeout,
    LoadingSpinner,
    Genre
  ) {

    $scope.sortField = 'name';
    $scope.reverse = false;

    var spinner = new LoadingSpinner($scope, 1);
    spinner.start();

	  $scope.genres = Genre.query(spinner.checkDoneLoading);
  }
]);
