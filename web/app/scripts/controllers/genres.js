'use strict';

/**
 * @ngdoc function
 * @name musicApp.controller:GenresCtrl
 * @description
 * # GenresCtrl
 * Controller of the musicApp
 */
angular.module('musicApp')
  .controller('GenresCtrl', ['$scope', 'Genre', 'usSpinnerService',
    function($scope, Genre, usSpinnerService) {

    $scope.sortField = 'name';
    $scope.reverse = false;
    $scope.doneLoading = false;

	  $scope.genres = Genre.query(function () {
      usSpinnerService.stop('spinner-loading');
      $scope.doneLoading = true;
    });
  }]);
