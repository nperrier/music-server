'use strict';

/**
 * @ngdoc function
 * @name musicApp.controller:AlbumsCtrl
 * @description
 * # AlbumsCtrl
 * Controller of the musicApp
 */
angular.module('musicApp')
  .controller('AlbumsCtrl', ['$scope', '$log', 'Album', 'AlbumTrack', 'Playlist', 'PlayerQueue', 'usSpinnerService',
    function($scope, $log, Album, AlbumTrack, Playlist, PlayerQueue, usSpinnerService) {

    $scope.sortField = 'name';
    $scope.reverse = false;
    $scope.doneLoading = false;

	  $scope.albums = Album.query(function () {
      usSpinnerService.stop('spinner-loading');
      $scope.doneLoading = true;
    });

    // this is needed for the album-action-menu modal
    $scope.playlists = Playlist.query();

    $scope.addAlbumToPlaylist = function(album, playlist) {
      $log.info('Add album.id: ' + album.id + ' to playlist.id: ' + playlist.id);

      AlbumTrack.get({ albumId: album.id }, function(tracks) {
        var trackIds = _.pluck(tracks, 'id');
        $log.info('Add track ids: ' + trackIds + ' to playlist.id: ' + playlist.id);
        Playlist.addTracks({ playlistId: playlist.id }, trackIds);
      });
    };

    // Add an Album to the player queue:
    $scope.addAlbumToQueue = function(album) {
      $log.info('Add album to player queue, id: ' + album.id);

      AlbumTrack.get({ albumId: album.id }, function(tracks) {
         var trackIds = _.pluck(tracks, 'id');
        $log.info('Add track ids: ' + trackIds + ' to player queue');
        PlayerQueue.addTracks(tracks);
      });
    };
  }]);

