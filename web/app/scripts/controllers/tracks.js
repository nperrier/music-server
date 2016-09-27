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
  'LoadingSpinner',
  'Track',
  'Playlist',
  function(
    $scope,
    $log,
    $timeout,
    LoadingSpinner,
    Track,
    Playlist
  ) {

    $scope.sortField = 'name';
    $scope.reverse = false;

    var spinner = new LoadingSpinner($scope, 2);
    spinner.start();

    // this is needed for the track-action-menu modal
    $scope.playlists = Playlist.query(spinner.checkDoneLoading);

    $scope.tracks = Track.query(spinner.checkDoneLoading);
  }
]);
