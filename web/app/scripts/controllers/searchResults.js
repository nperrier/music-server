'use strict';

/**
 * @ngdoc function
 * @name musicApp.controller:SearchCtrl
 * @description
 * # SearchCtrl
 * Controller of the musicApp
 */
angular.module('musicApp').controller('SearchResultsCtrl', [
  '$scope',
  '$log',
  '$timeout',
  '$stateParams',
  'LoadingSpinner',
  'Search',
  function(
    $scope,
    $log,
    $timeout,
    $stateParams,
    LoadingSpinner,
    Search
  ) {

    $scope.searchLimit = 10;
    $scope.query = $stateParams.q;

    $scope.artists = [];
    $scope.albums = [];
    $scope.tracks = [];

    var spinner = new LoadingSpinner($scope, 1);
    spinner.start();

    Search.query({ q: $stateParams.q }).$promise.then(function(results) {
      $scope.artists = results.artists || [];
      $scope.artistsTotal = results.artistsTotal;

      $scope.albums = results.albums || [];
      $scope.albumsTotal = results.albumsTotal;

      $scope.tracks = results.tracks || [];
      $scope.tracksTotal = results.tracksTotal;

      spinner.checkDoneLoading();
    });
  }
]);
