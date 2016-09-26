'use strict';

/**
 * @ngdoc function
 * @name musicApp.controller:AlbumSearchResultsCtrl
 * @description
 * # AlbumSearchResultsCtrl
 * Controller of the musicApp
 */
angular.module('musicApp').controller('AlbumSearchResultsCtrl', [
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
    $scope.albums = [];

    Search.get({ q: $stateParams.q, table: 'album' }).$promise.then(function(results) {
      $scope.albums = results.albums;
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
