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
  'Album',
  'Playlist',
  'LoadingSpinner',
  function(
    $scope,
    $log,
    $timeout,
    Album,
    Playlist,
    LoadingSpinner
  ) {

    var spinner = new LoadingSpinner($scope, 2);
    spinner.start();

    $scope.albums = Album.query(spinner.checkDoneLoading);

    // this is needed for the track-action-menu modal
    $scope.playlists = Playlist.query(spinner.checkDoneLoading);
  }
]);
