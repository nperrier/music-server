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
  }]);
