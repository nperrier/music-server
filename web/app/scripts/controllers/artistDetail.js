'use strict';

/**
 * @ngdoc function
 * @name musicApp.controller:ArtistDetailCtrl
 * @description
 * # ArtistDetailCtrl
 * Controller of the musicApp
 */

angular.module('musicApp').controller('ArtistDetailCtrl', [
    '$scope',
    '$stateParams',
    '$log',
    '$timeout',
    'usSpinnerService',
    'Artist',
    'Album',
    function(
      $scope,
      $stateParams,
      $log,
      $timeout,
      usSpinnerService,
      Artist,
      Album) {

      $scope.sortField = 'name';
      $scope.reverse = false;
      $scope.doneLoading = false;

      var numberPendingRequests = 2;

      // wait 1.5 seconds before showing spinner
      $timeout(function () {
        if (!$scope.doneLoading) {
          usSpinnerService.spin('spinner-loading');
        }
      }, 1500);

      var checkDoneLoading = function() {
        numberPendingRequests--;
        if (numberPendingRequests <= 0) {
          usSpinnerService.stop('spinner-loading');
          $scope.doneLoading = true;
        }
      };

      // Load artist from rest resource
      $scope.artist = Artist.get({ artistId: $stateParams.id }, checkDoneLoading);

      // Load albums from rest resource
      $scope.albums = Artist.getAlbums({ artistId: $stateParams.id }, checkDoneLoading);
    }
  ]);
