'use strict';

/**
 * @ngdoc directive
 * @name musicApp.directive:playlistActionMenu
 * @description
 * # playlistActionMenu
 */
angular.module('musicApp').directive('playlistActionMenu', [
  '$log',
  '$uibModal',
  'Playlist',
  'PlayerQueue',
  function(
    $log,
    $uibModal,
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

        scope.editPlaylist = function() {

          var modalInstance = $uibModal.open({
            templateUrl: 'views/editPlaylistModal.html',
            backdrop: false,
            resolve: {
             playlist: function () {
                return scope.playlist;
              }
            },
            controller: function ($scope, $uibModalInstance, playlist) {
              // copy playlist
              $scope.playlist = angular.copy(playlist);

              $scope.save = function (playlist) {
                $log.debug('Updating playlist, id: ' + playlist.id);
                var update = Playlist.update({ playlistId: playlist.id }, playlist);
                update.$promise.then(
                  function(playlist) {
                    $uibModalInstance.close(playlist);
                  },
                  function (error) {
                    $log.debug('Error updating playlist, id: ' + playlist.id + ', error: ' + error);
                    $scope.error = true;
                  }
                );
              };

              $scope.cancel = function (reason) {
                reason = reason || 'cancelled';
                $uibModalInstance.dismiss(reason);
              };
            }
          });

          modalInstance.result.then(
            function (playlist) {
              scope.playlist = playlist;
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
          var modalInstance = $uibModal.open({
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
            controller: function ($scope, $uibModalInstance, playlist, index) {
              $scope.playlist = playlist;

              $scope.ok = function (playlist) {
                $log.debug('Remove playlist, id: ' + playlist.id);
                $uibModalInstance.close(playlist, index);
              };

              $scope.cancel = function () {
                $uibModalInstance.dismiss();
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
