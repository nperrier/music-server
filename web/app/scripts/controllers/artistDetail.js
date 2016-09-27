'use strict';

/**
 * @ngdoc function
 * @name musicApp.controller:ArtistDetailCtrl
 * @description
 * # ArtistDetailCtrl
 * Controller of the musicApp
 */
angular.module('musicApp').controller('ArtistDetailCtrl', [
  '$scope',
  '$stateParams',
  '$log',
  '$timeout',
  'LoadingSpinner',
  'Artist',
  'Album',
  'Playlist',
  function(
    $scope,
    $stateParams,
    $log,
    $timeout,
    LoadingSpinner,
    Artist,
    Album,
    Playlist
  ) {

    $scope.sortField = 'name';
    $scope.reverse = false;

    var spinner = new LoadingSpinner($scope, 3);
    spinner.start();

    // Load artist from rest resource
    $scope.artist = Artist.get({ artistId: $stateParams.id }, spinner.checkDoneLoading);

    // Load albums from rest resource
    $scope.albums = Artist.getAlbums({ artistId: $stateParams.id }, spinner.checkDoneLoading);

    // this is needed for the track-action-menu modal
    $scope.playlists = Playlist.query(spinner.checkDoneLoading);

  }
]);
