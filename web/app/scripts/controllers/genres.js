'use strict';

/**
 * @ngdoc function
 * @name musicApp.controller:GenresCtrl
 * @description
 * # GenresCtrl
 * Controller of the musicApp
 */
angular.module('musicApp')
  .controller('GenresCtrl', ['$scope', 'Genre', function($scope, Genre) {

    $scope.sortField = 'name';
    $scope.reverse = true;
    $scope.doneLoading = false;

	  $scope.genres = Genre.query(function () {
      $scope.doneLoading = true;
    });
  }]);
