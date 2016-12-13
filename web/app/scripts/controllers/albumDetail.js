'use strict';

/**
 * @ngdoc function
 * @name musicApp.controller:AlbumDetailCtrl
 * @description
 * # AlbumDetailCtrl
 * Controller of the musicApp
 */
 angular.module('musicApp').controller('AlbumDetailCtrl', [
  '$scope',
  '$stateParams',
  '$log',
  '$timeout',
  '$q',
  'LoadingSpinner',
  'Album',
  'Track',
  'Playlist',
  'PlayerQueue',
  'User',
  function(
    $scope,
    $stateParams,
    $log,
    $timeout,
    $q,
    LoadingSpinner,
    Album,
    Track,
    Playlist,
    PlayerQueue,
    User
  ) {

  	$scope.sortField = 'number';
  	$scope.reverse = false;
    $scope.variousArtists = false; /* whether the album is a 'Various Artists' */

    var spinner = new LoadingSpinner($scope);
    spinner.start();

    /* TODO: compare by artist.name? */
    var isVariousArtists = function(tracks) {
      if (!tracks.length) {
        return false;
      }
      var artist = tracks[0].artist;
      for (var i = 0; i < tracks.length; i++) {
        if (artist.id !== tracks[i].artist.id) {
          $log.debug('artist.id: ' + artist.id + ' <=> ' + tracks[i].artist.id);
          return true;
        }
      }
    };

    $scope.loadAlbumDetails = function () {
      $q.all({
        album: Album.get({ albumId: $stateParams.id }).$promise.then(function(album) {
          album.downloadUrl += '?token=' + User.getToken();
          spinner.checkDoneLoading();
          return $q.resolve(album);
        }),
        playlists: Playlist.query().$promise,
        tracks: Album.getTracks({ albumId: $stateParams.id }).$promise.then(function(tracks) {
          tracks.forEach(function(t) {
            t.downloadUrl += '?token=' + User.getToken();
          });
          $scope.variousArtists = isVariousArtists(tracks);
          return $q.resolve(tracks);
        })
      }).then(function(result) {
        $scope.album = result.album;
        $scope.playlists = result.playlists;
        $scope.tracks = result.tracks;
        spinner.checkDoneLoading();
      });
    };

    // TODO: consider cases where album artist changes or no tracks are left
    // need to redirect back to previous page or something
    $scope.loadAlbumDetails();

    $scope.playAlbum = function(album) {
      $log.debug('Add album to player queue, id: ' + album.id);
      PlayerQueue.playTracksNow($scope.tracks);
    };
  }
]);
