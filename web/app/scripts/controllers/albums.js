'use strict';

/**
 * @ngdoc function
 * @name musicApp.controller:AlbumsCtrl
 * @description
 * # AlbumsCtrl
 * Controller of the musicApp
 */
angular.module('musicApp')
  .controller('AlbumsCtrl', ['$scope', '$log', '$timeout', '_',
    'Album', 'AlbumTrack', 'Playlist', 'PlayerQueue', 'usSpinnerService',
    function($scope, $log, $timeout, _,
      Album, AlbumTrack, Playlist, PlayerQueue, usSpinnerService) {

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
      $log.info('Add album.id: ' + album.id + ' to playlist.id: ' + playlist.id);
       Playlist.addAlbum({ playlistId: playlist.id }, album.id);
    };

    // Add an Album to the player queue:
    $scope.addAlbumToQueue = function(album) {
      $log.info('Add album to player queue, id: ' + album.id);

      AlbumTrack.get({ albumId: album.id }, function(tracks) {
        var orderedTracks = _.sortBy(tracks, function(t) { return t.number; });
        PlayerQueue.addTracks(orderedTracks);
      });
    };
  }]);
