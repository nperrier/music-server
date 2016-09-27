'use strict';

/**
 * @ngdoc function
 * @name musicApp.controller:GenreTracksCtrl
 * @description
 * # GenreTracksCtrl
 * Controller of the musicApp
 */
angular.module('musicApp').controller('GenreTracksCtrl', [
  '$scope',
  '$stateParams',
  '$log',
  '$timeout',
  'LoadingSpinner',
  'Genre',
  'Track',
  'Playlist',
  'PlayerQueue',
  function(
    $scope,
    $stateParams,
    $log,
    $timeout,
    LoadingSpinner,
    Genre,
    Track,
    Playlist,
    PlayerQueue
  ) {

    $scope.sortField = 'name';
    $scope.reverse = false;

    var spinner = new LoadingSpinner($scope, 2);
    spinner.start();

    // this is needed for the track-action-menu modal
    $scope.playlists = Playlist.query(spinner.checkDoneLoading);

    $scope.tracks = Genre.getTracks({ genreId: $stateParams.id }, spinner.checkDoneLoading);

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
