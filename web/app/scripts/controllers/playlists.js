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
  '$uibModal',
  '$timeout',
  'LoadingSpinner',
  'Playlist',
  function(
    $scope,
    $log,
    $uibModal,
    $timeout,
    LoadingSpinner,
    Playlist
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
      var modalInstance = $uibModal.open({
        templateUrl: 'views/playlistCreateModal.html',
        backdrop: false,
        resolve: {},
        controller: function ($scope, $uibModalInstance) {
          $scope.save = function (playlist) {
            // TODO: Need to add client-side validation
            $uibModalInstance.close(playlist);
          };

          $scope.cancel = function () {
            $uibModalInstance.dismiss('cancelled');
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
