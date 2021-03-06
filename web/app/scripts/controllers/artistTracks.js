'use strict';

/**
 * @ngdoc function
 * @name musicApp.controller:ArtisttracksCtrl
 * @description
 * # ArtisttracksCtrl
 * Controller of the musicApp
 */
angular.module('musicApp').controller('ArtistTracksCtrl', [
  '$scope',
  '$stateParams',
  '$log',
  '$timeout',
  '$q',
  'LoadingSpinner',
  'Artist',
  'Track',
  'Playlist',
  'User',
  'PlayerQueue',
  function(
    $scope,
    $stateParams,
    $log,
    $timeout,
    $q,
    LoadingSpinner,
    Artist,
    Track,
    Playlist,
    User,
    PlayerQueue
  ) {

    $scope.sortField = 'name';
    $scope.reverse = false;

    var spinner = new LoadingSpinner($scope);
    spinner.start();

    $scope.loadTracks = function () {
      $q.all({
        playlists: Playlist.query().$promise,
        artist: Artist.get({ artistId: $stateParams.id }).$promise,
        tracks: Artist.getTracks({ artistId: $stateParams.id }).$promise.then(function(tracks) {
          tracks.forEach(function(t) {
            t.downloadUrl += '?token=' + User.getToken();
          });
          return $q.resolve(tracks);
        })
      }).then(function(result) {
        $scope.artist = result.artist;
        $scope.playlists = result.playlists;
        $scope.tracks = result.tracks;
        spinner.checkDoneLoading();
      });
    };

    $scope.loadTracks();

    $scope.playTrack = function(track) {
      $log.debug('Add track to player queue, id: ' + track.id);
      PlayerQueue.playTrackNow(track);
    };
  }
]);

