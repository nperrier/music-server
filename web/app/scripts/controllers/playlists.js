'use strict';

/**
 * @ngdoc function
 * @name musicApp.controller:PlaylistsCtrl
 * @description
 * # PlaylistsCtrl
 * Controller of the musicApp
 */
angular.module('musicApp').controller('PlaylistsCtrl', [
  '$scope',
  '$log',
  '$modal',
  '$timeout',
  'LoadingSpinner',
  'Playlist',
  'PlayerQueue',
  function(
    $scope,
    $log,
    $modal,
    $timeout,
    LoadingSpinner,
    Playlist,
    PlayerQueue
  ) {

    $scope.sortField = 'name';

    var spinner = new LoadingSpinner($scope, 1);
    spinner.start();

    $scope.playlists = Playlist.query(spinner.checkDoneLoading);

    $scope.createPlaylist = function(playlist) {
      Playlist.save(playlist, function (p) {
        $scope.playlists.push(p);
      });
    };

    $scope.deletePlaylist = function(playlist, index) {
      $log.debug('Deleting playlist, id: ' + playlist.id);
      Playlist.delete({ playlistId: playlist.id }, function () {
        $scope.playlists.splice(index, 1);
      });
    };

    $scope.addPlaylistToQueue = function(playlist) {
      $log.debug('Add playlist to queue, id: ' + playlist.id);

      $scope.tracks = Playlist.getTracks({ playlistId: playlist.id }, function(tracks) {
        tracks.forEach(function(playlistTrack) {
          $log.debug('Add track to player queue, id: ' + playlistTrack.track.id);
          PlayerQueue.addTrack(playlistTrack.track);
        });
      });
    };

    $scope.createPlaylistDialog = function() {
      var modalInstance = $modal.open({
        templateUrl: 'views/playlistCreateModal.html',
        backdrop: false,
        resolve: {},
        controller: function ($scope, $modalInstance) {
          $scope.save = function (playlist) {
            // TODO: Need to add client-side validation
            $modalInstance.close(playlist);
          };

          $scope.cancel = function () {
            $modalInstance.dismiss('cancelled');
          };
        }
      });

      modalInstance.result.then(
        function (playlist) {
          $scope.createPlaylist(playlist);
        },
        function (reason) {
          $log.debug('Modal dismissed: ' + reason);
        }
      );
    };
  }
]);
