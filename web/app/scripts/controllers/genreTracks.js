'use strict';

/**
 * @ngdoc function
 * @name musicApp.controller:GenreTracksCtrl
 * @description
 * # GenreTracksCtrl
 * Controller of the musicApp
 */
angular.module('musicApp').controller('GenreTracksCtrl', [
  '$scope', '$routeParams', '$log', '$timeout', 'usSpinnerService',
  'Genre', 'Track', 'Playlist', 'PlayerQueue',
  function($scope, $routeParams, $log, $timeout, usSpinnerService,
    Genre, Track, Playlist, PlayerQueue) {

    $scope.sortField = 'name';
    $scope.reverse = false;
    $scope.doneLoading = false;

    // wait 1.5 seconds before showing spinner
    $timeout(function () {
      if (!$scope.doneLoading) {
        usSpinnerService.spin('spinner-loading');
      }
    }, 1500);

    // this is needed for the track-action-menu modal
    $scope.playlists = Playlist.query();

    $scope.tracks = Genre.getTracks({ genreId: $routeParams.genreId }, function() {
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
      $log.debugdebug('Added track to player queue, track.id: ' + track.id);
    };
  }
]);