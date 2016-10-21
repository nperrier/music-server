'use strict';

/**
 * @ngdoc directive
 * @name musicApp.directive:playlistActionMenu
 * @description
 * # playlistActionMenu
 */
angular.module('musicApp').directive('playlistActionMenu', [
  '$log',
  '$modal',
  'Playlist',
  'PlayerQueue',
  function(
    $log,
    $modal,
    Playlist,
    PlayerQueue
  ) {

    return {
      restrict: 'E',
      templateUrl: '/views/playlistActionMenu.html',
      scope: {
        playlist: '='
      },
      link: function(scope) {

        scope.updatePlaylist = function(playlist) {
          $log.debug('Updating playlist, id: ' + playlist.id);
          Playlist.update({ playlistId: playlist.id }, playlist, function (playlist) {
            // update the playlist
            scope.playlist = playlist;
          });
        };

        scope.editPlaylist = function() {

          var modalInstance = $modal.open({
            templateUrl: 'views/playlistEditModal.html',
            backdrop: false,
            resolve: {
             playlist: function () {
                return scope.playlist;
              }
            },
            controller: function ($scope, $modalInstance, playlist) {
              // copy playlist
              $scope.playlist = angular.copy(playlist);

              $scope.save = function (playlist) {
                $modalInstance.close(playlist);
              };

              $scope.cancel = function () {
                $modalInstance.dismiss('cancelled');
              };
            }
          });

          modalInstance.result.then(
            function (playlist) {
              scope.updatePlaylist(playlist);
            },
            function (reason) {
              $log.debug('Modal dismissed: ' + reason);
            }
          );
        };

        scope.addPlaylistToQueue = function(playlist) {
          $log.debug('Add playlist to queue, id: ' + playlist.id);

          scope.tracks = Playlist.getTracks({ playlistId: playlist.id }, function(tracks) {
            tracks.forEach(function(playlistTrack) {
              $log.debug('Add track to player queue, id: ' + playlistTrack.track.id);
              PlayerQueue.addTrack(playlistTrack.track);
            });
          });
        };

        scope.delete = function () {
          var modalInstance = $modal.open({
            templateUrl: 'views/playlistDeleteModal.html',
            size: 'sm',
            backdrop: false,
            resolve: {
              playlist: function () {
                return scope.playlist;
              },
              index: function() {
                return scope.$index;
              }
            },
            controller: function ($scope, $modalInstance, playlist, index) {
              $scope.playlist = playlist;

              $scope.ok = function (playlist) {
                $log.debug('Remove playlist, id: ' + playlist.id);
                $modalInstance.close(playlist, index);
              };

              $scope.cancel = function () {
                $modalInstance.dismiss();
              };
            }
          });

          modalInstance.result.then(
            function (playlist, index) {
              scope.deletePlaylist(playlist, index);
            },
            function () {
              $log.debug('Modal dismissed');
            }
          );
        };

        scope.deletePlaylist = function(playlist, index) {
          $log.debug('Deleting playlist, id: ' + playlist.id);
          Playlist.delete({ playlistId: playlist.id }, function () {
            scope.playlists.splice(index, 1);
          });
        };

      }
    };
  }
]);
