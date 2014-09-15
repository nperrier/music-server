'use strict';

angular.module('musicApp')
  .directive('trackActionMenu', ['$log', '$modal', 'Playlist', 'PlayerQueue',
    function($log, $modal, Playlist, PlayerQueue) {

    return {
      restrict: 'E',
      templateUrl: '/views/trackactionmenu.html',
      scope: {
        track: '='
      },
      controller: function ($scope, $element) {

        $scope.playlists = Playlist.query();

        // Add a track to the player queue:
        $scope.addToQueue = function(track) {
          $log.info('Add track to player queue, id: ' + track.id);
          PlayerQueue.addTrack(track);
        };

        $scope.addTrackToPlaylist = function (trackId) {

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
              Playlist.addTracks({ playlistId: playlist.id }, [ trackId ]);
              $log.info('Add trackId: ' + trackId + ' to playlistId: ' + playlist.id);
            },
            function (reason) {
              $log.info('Modal dismissed: ' + reason);
            }
          );
        };
      }
    };
  }]);
