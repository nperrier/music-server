'use strict';

/**
 * @ngdoc function
 * @name musicApp.controller:PlaylistDetailCtrl
 * @description
 * # PlaylistDetailCtrl
 * Controller of the musicApp
 */
angular.module('musicApp')
  .controller('PlaylistDetailCtrl', ['$scope', '$routeParams', '$timeout', 'Playlist', 'usSpinnerService',
    function($scope, $routeParams, $timeout, Playlist, usSpinnerService) {

    $scope.sortField = 'position';
    $scope.reverse = false;
    $scope.doneLoading = false;

    // wait 1.5 seconds before showing spinner
    $timeout(function () {
      if (!$scope.doneLoading) {
        usSpinnerService.spin('spinner-loading');
      }
    }, 1500);

    $scope.playlist = Playlist.get({ playlistId: $routeParams.playlistId });

    $scope.tracks = Playlist.getTracks({ playlistId: $routeParams.playlistId }, function(tracks) {
      usSpinnerService.stop('spinner-loading');
      $scope.doneLoading = true;
    });

  }]);
