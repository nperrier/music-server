'use strict';

/**
 * @ngdoc function
 * @name musicApp.controller:ArtistsCtrl
 * @description
 * # ArtistsCtrl
 * Controller of the musicApp
 */
angular.module('musicApp')
  .controller('ArtistsCtrl', ['$scope', 'Artist', 'usSpinnerService',
    function($scope, Artist, usSpinnerService) {

    $scope.sortField = 'name';
    $scope.reverse = false;
    $scope.doneLoading = false;

    $scope.artists = Artist.query(function () {
      usSpinnerService.stop('spinner-loading');
      $scope.doneLoading = true;
    });
  }]);
