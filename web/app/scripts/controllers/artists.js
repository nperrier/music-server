'use strict';

/**
 * @ngdoc function
 * @name musicApp.controller:ArtistsCtrl
 * @description
 * # ArtistsCtrl
 * Controller of the musicApp
 */
angular.module('musicApp').controller('ArtistsCtrl', [
  '$scope',
  '$timeout',
  'Artist',
  'LoadingSpinner',
  function(
    $scope,
    $timeout,
    Artist,
    LoadingSpinner
  ) {

    $scope.sortField = 'name';
    $scope.reverse = false;

    var spinner = new LoadingSpinner($scope, 1);
    spinner.start();

    $scope.artists = Artist.query(spinner.checkDoneLoading);
  }
]);
