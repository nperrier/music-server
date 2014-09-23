'use strict';

/**
 * @ngdoc function
 * @name musicApp.controller:PlaylistsCtrl
 * @description
 * # PlaylistsCtrl
 * Controller of the musicApp
 */
angular.module('musicApp')
  .controller('PlaylistsCtrl', ['$scope', 'Playlist', function ($scope, Playlist) {

    $scope.playlists = Playlist.query();
    $scope.sortField = 'name';

	  $scope.createPlaylist = function(playlist) {
		  Playlist.save(playlist).$promise.then(function () {
        // refresh the model so the UI updates after creating a new playlist
        $scope.playlists = Playlist.query();
      });
    };

  }]);
