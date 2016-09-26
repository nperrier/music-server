'use strict';

/**
 * @ngdoc function
 * @name musicApp.controller:AlbumsCtrl
 * @description
 * # AlbumsCtrl
 * Controller of the musicApp
 */
angular.module('musicApp').controller('AlbumsCtrl', [
  '$scope',
  '$log',
  '$timeout',
  'usSpinnerService',
  'Album',
  function(
    $scope,
    $log,
    $timeout,
    usSpinnerService,
    Album
  ) {

    $scope.doneLoading = false;

    // wait 1.5 seconds before showing spinner
    $timeout(function () {
      if (!$scope.doneLoading) {
        usSpinnerService.spin('spinner-loading');
      }
    }, 1500);

    $scope.albums = Album.query(function () {
      usSpinnerService.stop('spinner-loading');
      $scope.doneLoading = true;
    });

  }
]);
