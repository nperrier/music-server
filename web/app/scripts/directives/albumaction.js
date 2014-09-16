'use strict';
/* global _ */

/**
 * @ngdoc directive
 * @name musicApp.directive:albumAction
 * @description
 * # albumAction
 */
angular.module('musicApp')
  .directive('albumActionMenu', ['$log', '$modal', 'Playlist', 'PlayerQueue', 'AlbumTrack',
    function($log, $modal, Playlist, PlayerQueue, AlbumTrack) {

    return {
      restrict: 'E',
      templateUrl: '/views/albumactionmenu.html',
      scope: {
        album: '='
      },
      controller: function ($scope, $element) {

        $scope.playlists = Playlist.query();

        // Add an Album to the player queue:
        $scope.addAlbumToQueue = function(album) {

          $log.info('Add album to player queue, id: ' + album.id);

          AlbumTrack.get({ albumId: album.id }, function(tracks) {
            tracks.forEach(function(track) {
              $log.info('Add track to player queue, id: ' + track.id);
              PlayerQueue.addTrack(track);
            });
          });
        };

        $scope.addAlbumToPlaylist = function (album) {

          var modalInstance = $modal.open({
            templateUrl: 'views/playlistsmodal.html',
            size: 'sm',
            backdrop: false,
            resolve: {
              playlists: function () {
                return $scope.playlists;
              }
            },
            controller: function ($scope, $modalInstance, playlists) {

              $scope.playlists = playlists;

              $scope.selected = {
                playlist: $scope.playlists[0]
              };

              $scope.ok = function () {
                $modalInstance.close($scope.selected.playlist);
              };

              $scope.cancel = function () {
                $modalInstance.dismiss('cancelled');
              };
            }
          });

          modalInstance.result.then(
            function (playlist) {
              $scope.selected = playlist;

              $log.info('Add album.id: ' + album.id + ' to playlist.id: ' + playlist.id);

              AlbumTrack.get({ albumId: album.id }, function(tracks) {
                var trackIds = _.pluck(tracks, 'id');
                $log.info('Add track ids: ' + trackIds + ' to playlist.id: ' + playlist.id);
                Playlist.addTracks({ playlistId: playlist.id }, trackIds);
              });
            },
            function (reason) {
              $log.info('Modal dismissed: ' + reason);
            }
          );
        };
      }
    };
  }]);

