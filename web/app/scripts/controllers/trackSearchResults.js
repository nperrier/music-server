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
    $scope.tracks = [];

    Search.get({ q: $stateParams.q, table: 'track' }).$promise.then(function(results) {
      $scope.tracks = results.tracks;
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
