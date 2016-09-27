'use strict';

/**
 * @ngdoc function
 * @name musicApp.controller:TrackSearchResultsCtrl
 * @description
 * # TrackSearchResultsCtrl
 * Controller of the musicApp
 */
angular.module('musicApp').controller('TrackSearchResultsCtrl', [
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

    $scope.tracks = [];

    var spinner = new LoadingSpinner($scope, 1);
    spinner.start();

    Search.get({ q: $stateParams.q, table: 'track' }).$promise.then(function(results) {
      $scope.tracks = results.tracks;
      spinner.checkDoneLoading();
    });
  }
]);
