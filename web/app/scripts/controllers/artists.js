'use strict';

/**
 * @ngdoc function
 * @name musicApp.controller:ArtistsCtrl
 * @description
 * # ArtistsCtrl
 * Controller of the musicApp
 */
angular.module('musicApp')
  .controller('ArtistsCtrl', ['$scope', 'Artist', function($scope, Artist) {

    $scope.sortField = 'name';
    $scope.reverse = true;
    $scope.doneLoading = false;

    $scope.artists = Artist.query(function () {
      $scope.doneLoading = true;
    });
  }]);
