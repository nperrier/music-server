'use strict';

/**
 * @ngdoc function
 * @name musicApp.controller:ArtistsCtrl
 * @description
 * # ArtistsCtrl
 * Controller of the musicApp
 */
angular.module('musicApp')
  .controller('ArtistsCtrl', ['$scope', '$timeout', 'Artist', 'usSpinnerService',
    function($scope, $timeout, Artist, usSpinnerService) {

    $scope.sortField = 'name';
    $scope.reverse = false;
    $scope.doneLoading = false;

    // wait 1.5 seconds before showing spinner
    $timeout(function () {
      if (!$scope.doneLoading) {
        usSpinnerService.spin('spinner-loading');
      }
    }, 1500);

    $scope.artists = Artist.query(function () {
      usSpinnerService.stop('spinner-loading');
      $scope.doneLoading = true;
    });
  }]);
