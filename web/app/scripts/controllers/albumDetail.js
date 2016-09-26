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
  'usSpinnerService',
  'Album',
  'Track',
  'Playlist',
  'PlayerQueue',
  function(
    $scope,
    $stateParams,
    $log,
    $timeout,
    usSpinnerService,
    Album
  ) {

  	$scope.sortField = 'number';
  	$scope.reverse = false;
    $scope.variousArtists = false; /* whether the album is a 'Various Artists' */

    var numberPendingRequests = 2;

    // wait 1.5 seconds before showing spinner
    $timeout(function () {
      if (!$scope.doneLoading) {
        usSpinnerService.spin('spinner-loading');
      }
    }, 1500);

    var checkDoneLoading = function() {
      numberPendingRequests--;
      if (numberPendingRequests <= 0) {
        usSpinnerService.stop('spinner-loading');
        $scope.doneLoading = true;
      }
    };

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

  	Album.get({ albumId: $stateParams.id }, function(album) {
  		$scope.album = album;
      checkDoneLoading();
  	});

  	Album.getTracks({ albumId: $stateParams.id }, function(tracks) {
  		$scope.tracks = tracks;
      $scope.variousArtists = isVariousArtists(tracks);
      checkDoneLoading();
  	});

  }
]);
