'use strict';

/**
 * @ngdoc function
 * @name musicApp.controller:AlbumDetailCtrl
 * @description
 * # AlbumDetailCtrl
 * Controller of the musicApp
 */
angular.module('musicApp').controller('AlbumDetailCtrl', [
  '$scope', '$stateParams', '$log', '$timeout', 'usSpinnerService',
  'Album', 'Track', 'Playlist', 'PlayerQueue',
  function($scope, $stateParams, $log, $timeout, usSpinnerService,
    Album, Track, Playlist, PlayerQueue) {

  	$scope.sortField = 'number';
  	$scope.reverse = false;
    $scope.variousArtists = false; /* whether the album is a 'Various Artists' */
    var numberPendingRequests = 3;

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

    $scope.playlists = Playlist.query(checkDoneLoading);

  	Album.get({ albumId: $stateParams.id }, function(album) {
  		$scope.album = album;
      checkDoneLoading();
  	});

  	Album.getTracks({ albumId: $stateParams.id }, function(tracks) {
  		$scope.tracks = tracks;
      $scope.variousArtists = isVariousArtists(tracks);
      checkDoneLoading();
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
