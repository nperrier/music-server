'use strict';

/**
 * @ngdoc function
 * @name musicApp.controller:AlbumDetailCtrl
 * @description
 * # AlbumDetailCtrl
 * Controller of the musicApp
 */
angular.module('musicApp').controller('AlbumDetailCtrl', [
  '$scope',
  '$stateParams',
  '$log',
  '$timeout',
  'LoadingSpinner',
  'Album',
  'Track',
  'Playlist',
  function(
    $scope,
    $stateParams,
    $log,
    $timeout,
    LoadingSpinner,
    Album,
    Track,
    Playlist
  ) {

  	$scope.sortField = 'number';
  	$scope.reverse = false;
    $scope.variousArtists = false; /* whether the album is a 'Various Artists' */

    var spinner = new LoadingSpinner($scope, 3);
    spinner.start();

    /* TODO: compare by artist.name? */
    var isVariousArtists = function(tracks) {
      if (tracks.length) {
        var artist = tracks[0].artist;
        for (var i = 0; i < tracks.length; i++) {
          if (artist.id !== tracks[i].artist.id) {
            $log.debug('artist.id: ' + artist.id + ' <=> ' + tracks[i].artist.id);
            return true;
          }
        }
      }
      return false;
    };

    // this is needed for the track-action-menu modal
    $scope.playlists = Playlist.query(spinner.checkDoneLoading);

  	$scope.album = Album.get({ albumId: $stateParams.id }, spinner.checkDoneLoading);

  	Album.getTracks({ albumId: $stateParams.id }, function(tracks) {
  		$scope.tracks = tracks;
      $scope.variousArtists = isVariousArtists(tracks);
      spinner.checkDoneLoading();
  	});
  }
]);
