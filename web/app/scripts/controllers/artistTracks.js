'use strict';

/**
 * @ngdoc function
 * @name musicApp.controller:ArtisttracksCtrl
 * @description
 * # ArtisttracksCtrl
 * Controller of the musicApp
 */
angular.module('musicApp').controller('ArtistTracksCtrl', [
  '$scope',
  '$stateParams',
  '$log',
  '$timeout',
  'usSpinnerService',
  'Artist',
  'Track',
  'Playlist',
  'PlayerQueue',
    function(
      $scope,
      $stateParams,
      $log,
      $timeout,
      usSpinnerService,
      Artist,
      Track,
      PlayerQueue) {

    $scope.sortField = 'name';
    $scope.reverse = false;
    $scope.doneLoading = false;

    var numberPendingRequests = 2;

    var checkDoneLoading = function() {
      numberPendingRequests--;
      if (numberPendingRequests <= 0) {
        usSpinnerService.stop('spinner-loading');
        $scope.doneLoading = true;
      }
    };

    $scope.artist = Artist.get({ artistId: $stateParams.id }, checkDoneLoading);

    $scope.tracks = Artist.getTracks({ artistId: $stateParams.id }, checkDoneLoading);

    // wait 1.5 seconds before showing spinner
    $timeout(function () {
      if (!$scope.doneLoading) {
        usSpinnerService.spin('spinner-loading');
      }
    }, 1500);

  }
]);

