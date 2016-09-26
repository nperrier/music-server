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
  'usSpinnerService',
  'Search',
  function(
    $scope,
    $log,
    $timeout,
    $stateParams,
    usSpinnerService,
    Search
  ) {

    $scope.doneLoading = false;
    $scope.searchLimit = 10;
    $scope.query = $stateParams.q;

    $scope.artists = [];
    $scope.albums = [];
    $scope.tracks = [];


    Search.query({ q: $stateParams.q }).$promise.then(function(results) {
      $scope.artists = results.artists || [];
      $scope.artistsTotal = results.artistsTotal;

      $scope.albums = results.albums || [];
      $scope.albumsTotal = results.albumsTotal;

      $scope.tracks = results.tracks || [];
      $scope.tracksTotal = results.tracksTotal;

      usSpinnerService.stop('spinner-loading');
      $scope.doneLoading = true;
    });

    // wait 1.5 seconds before showing spinner
    $timeout(function () {
      if (!$scope.doneLoading) {
        usSpinnerService.spin('spinner-loading');
      }
    }, 1500);

}]);
