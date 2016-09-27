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
  'LoadingSpinner',
  'Artist',
  'Track',
  'Playlist',
  function(
    $scope,
    $stateParams,
    $log,
    $timeout,
    LoadingSpinner,
    Artist,
    Track,
    Playlist
  ) {

    $scope.sortField = 'name';
    $scope.reverse = false;

    var spinner = new LoadingSpinner($scope, 3);
    spinner.start();

    $scope.artist = Artist.get({ artistId: $stateParams.id }, spinner.checkDoneLoading);

    $scope.tracks = Artist.getTracks({ artistId: $stateParams.id }, spinner.checkDoneLoading);

    // this is needed for the track-action-menu modal
    $scope.playlists = Playlist.query(spinner.checkDoneLoading);
  }
]);

