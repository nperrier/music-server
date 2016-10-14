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

    var spinner = new LoadingSpinner($scope);
    spinner.start();

    Artist.query(function(artists) {
      $scope.artists = artists;
      spinner.checkDoneLoading();
    });
  }
]);
