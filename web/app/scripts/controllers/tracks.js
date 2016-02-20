'use strict';

/**
 * @ngdoc function
 * @name musicApp.controller:TracksCtrl
 * @description
 * # TracksCtrl
 * Controller of the musicApp
 */
angular.module('musicApp').controller('TracksCtrl', [
  '$scope', '$log', '$timeout', 'usSpinnerService', 'Track', 'Playlist', 'PlayerQueue',
    function($scope, $log, $timeout, usSpinnerService, Track, Playlist, PlayerQueue) {

    $scope.sortField = 'name';
    $scope.reverse = false;
    $scope.doneLoading = false;

    // this is needed for the track-action-menu modal
    $scope.playlists = Playlist.query();

    // wait 1.5 seconds before showing spinner
    $timeout(function () {
      if (!$scope.doneLoading) {
        usSpinnerService.spin('spinner-loading');
      }
    }, 1500);

    $scope.tracks = Track.query(function () {
      usSpinnerService.stop('spinner-loading');
      $scope.doneLoading = true;
    });

    $scope.updateTrack = function(trackId, trackInfo) {
      $log.debug('updateTrack, trackId: ' + trackId);

      Track.update({ trackId: trackId }, trackInfo, function () {
        // do something after updating
      });
    };

    $scope.addTrackToPlaylist = function(track, playlist) {
      Playlist.addTracks({ playlistId: playlist.id }, [ track.id ]);
      $log.debug('Added track.id: ' + track.id + ' to playlist.id: ' + playlist.id);
    };

    $scope.addTrackToQueue = function(track) {
      PlayerQueue.addTrack(track);
      $log.debug('Added track to player queue, track.id: ' + track.id);
    };
  }
]);
