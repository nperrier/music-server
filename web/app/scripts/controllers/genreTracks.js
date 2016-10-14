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
  '$q',
  'LoadingSpinner',
  'Genre',
  'Track',
  'Playlist',
  'PlayerQueue',
  'User',
  function(
    $scope,
    $stateParams,
    $log,
    $timeout,
    $q,
    LoadingSpinner,
    Genre,
    Track,
    Playlist,
    PlayerQueue,
    User
  ) {

    $scope.sortField = 'name';
    $scope.reverse = false;

    var spinner = new LoadingSpinner($scope);
    spinner.start();

    $q.all({
      tracks: Genre.getTracks({ genreId: $stateParams.id }).$promise.then(function(tracks) {
        tracks.forEach(function(t) {
          t.downloadUrl += '?token=' + User.getToken();
        });
        return $q.resolve(tracks);
      }),
      playlists: Playlist.query().$promise
    }).then(function(result) {
      $scope.tracks = result.tracks;
      $scope.playlists = result.playlists;
      spinner.checkDoneLoading();
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
