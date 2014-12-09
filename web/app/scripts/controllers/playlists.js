'use strict';

/**
 * @ngdoc function
 * @name musicApp.controller:PlaylistsCtrl
 * @description
 * # PlaylistsCtrl
 * Controller of the musicApp
 */
angular.module('musicApp')
  .controller('PlaylistsCtrl', ['$scope', '$log', '$modal', '$timeout', 'Playlist', 'PlayerQueue', 'usSpinnerService',
    function ($scope, $log, $modal, $timeout, Playlist, PlayerQueue, usSpinnerService) {

    $scope.sortField = 'name';
    $scope.doneLoading = false;

    // wait 1.5 seconds before showing spinner
    $timeout(function () {
      if (!$scope.doneLoading) {
        usSpinnerService.spin('spinner-loading');
      }
    }, 1500);

    $scope.playlists = Playlist.query(function() {
      usSpinnerService.stop('spinner-loading');
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

    $scope.addPlaylistToQueue = function(playlist) {
      $log.info('Add playlist to queue, id: ' + playlist.id);

      $scope.tracks = Playlist.getTracks({ playlistId: playlist.id }, function(tracks) {
        tracks.forEach(function(playlistTrack) {
          $log.info('Add track to player queue, id: ' + playlistTrack.track.id);
          PlayerQueue.addTrack(playlistTrack.track);
        });
      });
    };

    $scope.createPlaylistDialog = function() {

      var modalInstance = $modal.open({
        templateUrl: 'views/playlistcreatemodal.html',
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
          $log.info('Modal dismissed: ' + reason);
        }
      );
    };

  }]);
