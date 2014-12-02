'use strict';

/**
 * @ngdoc function
 * @name musicApp.controller:GenreTracksCtrl
 * @description
 * # GenreTracksCtrl
 * Controller of the musicApp
 */
angular.module('musicApp')
  .controller('GenreTracksCtrl', ['$scope', '$routeParams', '$log', 'GenreTrack', 'Track', 'Playlist', 'PlayerQueue', 'usSpinnerService',
    function($scope, $routeParams, $log, GenreTrack, Track, Playlist, PlayerQueue, usSpinnerService) {

    $scope.sortField = 'name';
    $scope.reverse = false;
    $scope.doneLoading = false;

    // this is needed for the track-action-menu modal
    $scope.playlists = Playlist.query();

    GenreTrack.get({ genreId: $routeParams.genreId }, function(tracks) {
      $scope.tracks = tracks;
      usSpinnerService.stop('spinner-loading');
      $scope.doneLoading = true;
    });

    $scope.updateTrack = function(trackId, trackInfo) {
      $log.info('updateTrack, trackId: ' + trackId);
      Track.update({ trackId: trackId }, trackInfo, function (t) {
        // do something after updating
      });
    };

    $scope.addTrackToPlaylist = function(track, playlist) {
      Playlist.addTracks({ playlistId: playlist.id }, [ track.id ]);
      $log.info('Added track.id: ' + track.id + ' to playlist.id: ' + playlist.id);
    };

    $scope.addTrackToQueue = function(track) {
      PlayerQueue.addTrack(track);
      $log.info('Added track to player queue, track.id: ' + track.id);
    };

  }]);
