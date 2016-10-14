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
  '$q',
  'LoadingSpinner',
  'Track',
  'Playlist',
  'User',
  function(
    $scope,
    $log,
    $timeout,
    $q,
    LoadingSpinner,
    Track,
    Playlist,
    User
  ) {

    $scope.sortField = 'name';
    $scope.reverse = false;

    var spinner = new LoadingSpinner($scope);
    spinner.start();

    $q.all({
      playlists: Playlist.query().$promise,
      tracks: Track.query().$promise.then(function(tracks) {
        tracks.forEach(function(t) {
          t.downloadUrl += '?token=' + User.getToken();
        });
        return $q.resolve(tracks);
      })
    }).then(function(result) {
      $scope.playlists = result.playlists;
      $scope.tracks = result.tracks;
      spinner.checkDoneLoading();
    });
  }
]);
