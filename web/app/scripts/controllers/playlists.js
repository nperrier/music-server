'use strict';

/**
 * @ngdoc function
 * @name musicApp.controller:PlaylistsCtrl
 * @description
 * # PlaylistsCtrl
 * Controller of the musicApp
 */
angular.module('musicApp')
  .controller('PlaylistsCtrl', ['$scope', '$log', 'Playlist', 'PlayerQueue', 'usSpinnerService',
    function ($scope, $log, Playlist, PlayerQueue, usSpinnerService) {

    $scope.sortField = 'name';
    $scope.doneLoading = false;

    $scope.playlists = Playlist.query(function() {
      usSpinnerService.stop('spinner-loading');
      $scope.doneLoading = true;
    });

	  $scope.createPlaylist = function(playlist) {
		  Playlist.save(playlist, function (p) {
        $scope.playlists.push(p);
      });
    };

    $scope.deletePlaylist = function(playlist) {
      $log.info('Deleting playlist, id: ' + playlist.id);
      Playlist.delete({ playlistId: playlist.id }, function () {
        // TODO: Might be better to simply remove the corresponding playlist object
        // from the Array instead of re-querying the entire list
        $scope.playlists = Playlist.query();
      });
    };

    $scope.addPlaylistToQueue = function(playlist) {
      $log.info('Add playlist to queue, id: ' + playlist.id);

      $scope.tracks = Playlist.getTracks({ playlistId: playlist.id }, function(tracks) {
        tracks.forEach(function(playlistTrack) {
          $log.info('Add track to player queue, id: ' + playlistTrack.track.id);
          PlayerQueue.addTrack(playlistTrack.track);
        });
      });
    };

  }]);
