'use strict';

/**
 * @ngdoc function
 * @name musicApp.controller:PlaylistsCtrl
 * @description
 * # PlaylistsCtrl
 * Controller of the musicApp
 */
angular.module('musicApp')
  .controller('PlaylistsCtrl', ['$scope', '$log', 'Playlist', function ($scope, $log, Playlist) {

    $scope.sortField = 'name';
    $scope.doneLoading = false;

    $scope.playlists = Playlist.query(function() {
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

  }]);
