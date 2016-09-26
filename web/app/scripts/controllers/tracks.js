'use strict';

/**
 * @ngdoc function
 * @name musicApp.controller:TracksCtrl
 * @description
 * # TracksCtrl
 * Controller of the musicApp
 */
angular.module('musicApp').controller('TracksCtrl', [
  '$scope',
  '$log',
  '$timeout',
  'usSpinnerService',
  'Track',
  function(
    $scope,
    $log,
    $timeout,
    usSpinnerService,
    Track
  ) {

    $scope.sortField = 'name';
    $scope.reverse = false;
    $scope.doneLoading = false;

    // wait 1.5 seconds before showing spinner
    $timeout(function () {
      if (!$scope.doneLoading) {
        usSpinnerService.spin('spinner-loading');
      }
    }, 1500);

    $scope.tracks = Track.query(function () {
      usSpinnerService.stop('spinner-loading');
      $scope.doneLoading = true;
    });
  }
]);
