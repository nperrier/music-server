'use strict';

/**
 * @ngdoc function
 * @name musicApp.controller:PlaylistDetailCtrl
 * @description
 * # PlaylistDetailCtrl
 * Controller of the musicApp
 */
angular.module('musicApp')
  .controller('PlaylistDetailCtrl', ['$scope', '$routeParams', 'Playlist', function($scope, $routeParams, Playlist) {

    Playlist.get({ playlistId: $routeParams.playlistId }, function(playlist) {
      $scope.playlist = playlist;
    });

    $scope.tracks = Playlist.getTracks({ playlistId: $routeParams.playlistId }, function(tracks) {
      $scope.tracks = tracks;
    });

    $scope.sortField = 'position';
    $scope.reverse = true;
  }]);
