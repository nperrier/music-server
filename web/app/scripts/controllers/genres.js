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
  '$log',
  'LoadingSpinner',
  'Genre',
  function(
    $scope,
    $timeout,
    $log,
    LoadingSpinner,
    Genre
  ) {

    $scope.sortField = 'name';
    $scope.reverse = false;

    var spinner = new LoadingSpinner($scope);
    spinner.start();

	  // $scope.genres = Genre.query(spinner.checkDoneLoading);
    Genre.query(function(genres) {
      $scope.genres = genres;
      spinner.checkDoneLoading();
    });
  }
]);
