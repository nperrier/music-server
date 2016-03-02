'use strict';

/**
 * @ngdoc function
 * @name musicApp.controller:AlbumsCtrl
 * @description
 * # AlbumsCtrl
 * Controller of the musicApp
 */
angular.module('musicApp').controller('AlbumsCtrl', [
  '$scope', '$log', '$timeout', '_', 'usSpinnerService',
  'Album', 'Playlist', 'PlayerQueue',
  function($scope, $log, $timeout, _, usSpinnerService,
    Album, Playlist, PlayerQueue) {

    $scope.sortField = 'name';
    $scope.reverse = false;
    $scope.doneLoading = false;

    // wait 1.5 seconds before showing spinner
    $timeout(function () {
      if (!$scope.doneLoading) {
        usSpinnerService.spin('spinner-loading');
      }
    }, 1500);

    $scope.albums = Album.query(function () {
      usSpinnerService.stop('spinner-loading');
      $scope.doneLoading = true;
    });

    // this is needed for the album-action-menu modal
    $scope.playlists = Playlist.query();

    $scope.addAlbumToPlaylist = function(album, playlist) {
      $log.debug('Add album.id: ' + album.id + ' to playlist.id: ' + playlist.id);
       Playlist.addAlbum({ playlistId: playlist.id, albumId: album.id });
    };

    // Add an Album to the player queue:
    $scope.addAlbumToQueue = function(album) {
      $log.debug('Add album to player queue, id: ' + album.id);
      Album.getTracks({ albumId: album.id }, function(tracks) {
        var orderedTracks = _.sortBy(tracks, function(t) { return t.number; });
        PlayerQueue.addTracks(orderedTracks);
      });
    };
  }
]);
