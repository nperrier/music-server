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
  '$q',
  'Album',
  'Playlist',
  'LoadingSpinner',
  'User',
  function(
    $scope,
    $log,
    $timeout,
    $q,
    Album,
    Playlist,
    LoadingSpinner,
    User
  ) {

    var spinner = new LoadingSpinner($scope);
    spinner.start();

    var loadAlbums = function() {
      $q.all({
        albums: Album.query().$promise.then(function(albums) {
          albums.forEach(function(a) {
            a.downloadUrl += '?token=' + User.getToken();
          });
          return $q.resolve(albums);
        }),
        playlists: Playlist.query()
      }).then(function(result) {
        $scope.albums = result.albums;
        $scope.playlists = result.playlists;
        spinner.checkDoneLoading();
      });
    };

    loadAlbums();

    $scope.reload = function () {
      loadAlbums();
    };
  }
]);
