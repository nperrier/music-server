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
  '$q',
  'LoadingSpinner',
  'Artist',
  'Album',
  'Playlist',
  'User',
  function(
    $scope,
    $stateParams,
    $log,
    $timeout,
    $q,
    LoadingSpinner,
    Artist,
    Album,
    Playlist,
    User
  ) {

    $scope.sortField = 'name';
    $scope.reverse = false;

    var spinner = new LoadingSpinner($scope);
    spinner.start();

    $q.all({
      artist: Artist.get({ artistId: $stateParams.id }).$promise,
      albums: Artist.getAlbums({ artistId: $stateParams.id }).$promise.then(function(albums) {
        albums.forEach(function(a) {
          a.downloadUrl += '?token=' + User.getToken();
        });
        return $q.resolve(albums);
      }),
      playlists: Playlist.query().$promise
    }).then(function(result) {
      $scope.artist = result.artist;
      $scope.albums = result.albums;
      $scope.playlists = result.playlists;
      spinner.checkDoneLoading();
    });
  }
]);
