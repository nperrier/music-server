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

    var spinner = new LoadingSpinner($scope);
    spinner.start();

    Playlist.query(function(playlists) {
      $scope.playlists = playlists;
      spinner.checkDoneLoading();
    });

    $scope.createPlaylist = function(playlist) {
      Playlist.save(playlist, function (p) {
        $scope.playlists.push(p);
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
