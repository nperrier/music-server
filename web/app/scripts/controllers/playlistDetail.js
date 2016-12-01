'use strict';

/**
 * @ngdoc function
 * @name musicApp.controller:PlaylistDetailCtrl
 * @description
 * # PlaylistDetailCtrl
 * Controller of the musicApp
 */
angular.module('musicApp').controller('PlaylistDetailCtrl', [
  '$scope',
  '$log',
  '$stateParams',
  '$timeout',
  '$q',
  'LoadingSpinner',
  '_',
  'Playlist',
  function(
    $scope,
    $log,
    $stateParams,
    $timeout,
    $q,
    LoadingSpinner,
    _,
    Playlist
  ) {

    var spinner = new LoadingSpinner($scope);
    spinner.start();

    $q.all({
      playlist: Playlist.get({ playlistId: $stateParams.id }).$promise,
      tracks: Playlist.getTracks({ playlistId: $stateParams.id }).$promise.then(function(tracks) {
        return $q.resolve(tracks);
      })
    }).then(function(result) {
      $scope.playlist = result.playlist;
      $scope.tracks = result.tracks;
      spinner.checkDoneLoading();
    });

    $scope.remove = function(playlistTrack, position) {
      $log.debug('Removing playlistTrack.id: ' + playlistTrack.id + ' at position: ' + position);
      var params = { playlistId: $stateParams.id, playlistTrackId: playlistTrack.id };
      Playlist.deleteTrack(params, function() {
        // remove track:
        $scope.tracks.splice(position, 1);
        // update positions of other tracks
        for (var i = position; i < $scope.tracks.length; i++) {
          $scope.tracks[i].position--;
        }
      },
      function(err) {
        $log.error('Unable to delete track ' + err);
      });
    };

    $scope.onTrackMoveSort = function($item, $partFrom, $partTo, $indexFrom, $indexTo) {
      $log.debug('move sorted, fromIndex: ' + $indexFrom + ', toIndex: ' + $indexTo);

      // update the position of all tracks in between to and from indexes:
      var startIndex;
      var endIndex;
      if ($indexFrom < $indexTo) {
        startIndex = $indexFrom;
        endIndex = $indexTo;
      } else {
        startIndex = $indexTo;
        endIndex = $indexFrom;
      }

      for (var i = startIndex; i <= endIndex; i++) {
        $scope.tracks[i].position = i;
      }

      Playlist.updateTracks({ playlistId: $stateParams.id }, $scope.tracks);
    };
  }
]);
