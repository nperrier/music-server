'use strict';

/**
 * @ngdoc function
 * @name musicApp.controller:TracksCtrl
 * @description
 * # TracksCtrl
 * Controller of the musicApp
 */
angular.module('musicApp')
  .controller('TracksCtrl', ['$scope', 'Track', function($scope, Track) {

    $scope.sortField = 'name';
    $scope.reverse = false;
    $scope.doneLoading = false;

    $scope.tracks = Track.query(function () {
      $scope.doneLoading = true;
    });
  }]);
