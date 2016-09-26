'use strict';

/**
 * @ngdoc function
 * @name musicApp.controller:ArtistSearchResultsCtrl
 * @description
 * # ArtistSearchResultsCtrl
 * Controller of the musicApp
 */
angular.module('musicApp').controller('ArtistSearchResultsCtrl', [
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
    $scope.artists = [];

    Search.get({ q: $stateParams.q, table: 'artist' }).$promise.then(function(results) {
      $scope.artists = results.artists;
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
