'use strict';

/**
 * @ngdoc function
 * @name musicApp.controller:ArtistDetailCtrl
 * @description
 * # ArtistDetailCtrl
 * Controller of the musicApp
 */
angular.module('musicApp').controller('ArtistDetailCtrl', [
  '_',
  '$scope',
  '$stateParams',
  '$log',
  '$timeout',
  '$q',
  'LoadingSpinner',
  'Artist',
  'Album',
  'Playlist',
  'PlayerQueue',
  'User',
  function(
    _,
    $scope,
    $stateParams,
    $log,
    $timeout,
    $q,
    LoadingSpinner,
    Artist,
    Album,
    Playlist,
    PlayerQueue,
    User
  ) {

    $scope.sortField = 'name';
    $scope.reverse = false;

    var spinner = new LoadingSpinner($scope);
    spinner.start();

    $scope.loadTracks = function () {
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
    };

    $scope.loadTracks();

    $scope.playAlbum = function(album) {
      $log.debug('Add album to player queue, id: ' + album.id);
      Album.getTracks({ albumId: album.id }, function(tracks) {
        var orderedTracks = _.sortBy(tracks, function(t) { return t.number; });
        PlayerQueue.playTracksNow(orderedTracks);
      });
    };
  }
]);
