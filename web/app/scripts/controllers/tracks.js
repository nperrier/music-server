'use strict';

/**
 * Controller for the playlists modal popup
 */
var AddTrackToPlaylistCtrl = function ($scope, $modalInstance, playlists) {

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
};

/**
 * @ngdoc function
 * @name musicApp.controller:TracksCtrl
 * @description
 * # TracksCtrl
 * Controller of the musicApp
 */
angular.module('musicApp')
  .controller('TracksCtrl', ['$scope', '$log', '$modal', 'Track', 'Playlist',
    function($scope, $log, $modal, Track, Playlist) {

  $scope.tracks = Track.query();
  $scope.playlists = Playlist.query();

  $scope.sortField = 'name';
  $scope.reverse = true;

  // Add a track to the player queue:
  $scope.addToQueue = function(trackId) {
    $log.info('Add track to player queue, id: ' + trackId);
  };

  $scope.addTrackToPlaylist = function (trackId) {

    var modalInstance = $modal.open({
      templateUrl: 'views/playlistsModal.html',
      controller: AddTrackToPlaylistCtrl,
      size: 'sm',
      backdrop: false,
      resolve: {
        playlists: function () {
          return $scope.playlists;
        }
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
}]);
