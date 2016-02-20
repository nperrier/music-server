'use strict';

/**
 * @ngdoc function
 * @name musicApp.controller:ArtisttracksCtrl
 * @description
 * # ArtisttracksCtrl
 * Controller of the musicApp
 */
angular.module('musicApp').controller('ArtistTracksCtrl', [
  '$scope', '$routeParams', '$log', '$timeout', 'usSpinnerService',
  'Artist', 'Track', 'Playlist', 'PlayerQueue',
    function($scope, $routeParams, $log, $timeout, usSpinnerService,
      Artist, Track, Playlist, PlayerQueue) {

    $scope.sortField = 'name';
    $scope.reverse = false;
    $scope.doneLoading = false;
    var numberPendingRequests = 3;

    var checkDoneLoading = function() {
      numberPendingRequests--;
      if (numberPendingRequests <= 0) {
        usSpinnerService.stop('spinner-loading');
        $scope.doneLoading = true;
      }
    };

    // this is needed for the track-action-menu modal
    $scope.playlists = Playlist.query(checkDoneLoading);

    $scope.artist = Artist.get({ artistId: $routeParams.artistId }, checkDoneLoading);

    $scope.tracks = Artist.getTracks({ artistId: $routeParams.artistId }, checkDoneLoading);

    // wait 1.5 seconds before showing spinner
    $timeout(function () {
      if (!$scope.doneLoading) {
        usSpinnerService.spin('spinner-loading');
      }
    }, 1500);

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

  }]);

