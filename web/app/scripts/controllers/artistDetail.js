'use strict';

/**
 * @ngdoc function
 * @name musicApp.controller:ArtistDetailCtrl
 * @description
 * # ArtistDetailCtrl
 * Controller of the musicApp
 */

angular.module('musicApp')
  .controller('ArtistDetailCtrl', [
    '$scope', '$routeParams', '$log', '$timeout', '_',
    'Artist', 'ArtistAlbum', 'Playlist', 'AlbumTrack',
    'PlayerQueue', 'usSpinnerService',
    function($scope, $routeParams, $log, $timeout, _,
      Artist, ArtistAlbum, Playlist, AlbumTrack,
      PlayerQueue, usSpinnerService) {

      $scope.sortField = 'name';
      $scope.reverse = false;
      $scope.doneLoading = false;
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

      // Load artist from rest resource
      $scope.artist = Artist.get({ artistId: $routeParams.artistId }, checkDoneLoading);

      // Load albums from rest resource
      $scope.albums = ArtistAlbum.get({ artistId: $routeParams.artistId }, checkDoneLoading);

      // this is needed for the album-action-menu modal
      $scope.playlists = Playlist.query(checkDoneLoading);

      $scope.addAlbumToPlaylist = function(album, playlist) {
        $log.info('Add album.id: ' + album.id + ' to playlist.id: ' + playlist.id);

        AlbumTrack.get({ albumId: album.id }, function(tracks) {
          var orderedTracks = _.sortBy(tracks, function(t) { return t.number; });
          var trackIds = _.pluck(orderedTracks, 'id');
          $log.info('Add track ids: ' + trackIds + ' to playlist.id: ' + playlist.id);
          Playlist.addTracks({ playlistId: playlist.id }, trackIds);
        });
      };

      // Add an Album to the player queue:
      $scope.addAlbumToQueue = function(album) {
        $log.info('Add album to player queue, id: ' + album.id);

        AlbumTrack.get({ albumId: album.id }, function(tracks) {
          var orderedTracks = _.sortBy(tracks, function(t) { return t.number; });
          PlayerQueue.addTracks(orderedTracks);
        });
      };
    }
  ]);
