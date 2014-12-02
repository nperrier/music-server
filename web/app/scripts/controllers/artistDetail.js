'use strict';

/**
 * @ngdoc function
 * @name musicApp.controller:ArtistDetailCtrl
 * @description
 * # ArtistDetailCtrl
 * Controller of the musicApp
 */

angular.module('musicApp')
  .controller('ArtistDetailCtrl', ['$scope', '$routeParams', '$log', 'Artist', 'ArtistAlbum', 'Playlist', 'AlbumTrack', 'PlayerQueue',
    function($scope, $routeParams, $log, Artist, ArtistAlbum, Playlist, AlbumTrack, PlayerQueue) {

      $scope.sortField = 'name';
      $scope.reverse = false;

      // Load artist from rest resource
      Artist.get({ artistId: $routeParams.artistId }, function(artist) {
        $scope.artist = artist;
      });

      // Load albums from rest resource
      ArtistAlbum.get({ artistId: $routeParams.artistId }, function(albums) {
        $scope.albums = albums;
      });

      // this is needed for the album-action-menu modal
      $scope.playlists = Playlist.query();

      $scope.addAlbumToPlaylist = function(album, playlist) {
        $log.info('Add album.id: ' + album.id + ' to playlist.id: ' + playlist.id);

        AlbumTrack.get({ albumId: album.id }, function(tracks) {
          var trackIds = _.pluck(tracks, 'id');
          $log.info('Add track ids: ' + trackIds + ' to playlist.id: ' + playlist.id);
          Playlist.addTracks({ playlistId: playlist.id }, trackIds);
        });
      };

      // Add an Album to the player queue:
      $scope.addAlbumToQueue = function(album) {
        $log.info('Add album to player queue, id: ' + album.id);

        AlbumTrack.get({ albumId: album.id }, function(tracks) {
          var trackIds = _.pluck(tracks, 'id');
          $log.info('Add track ids: ' + trackIds + ' to player queue');
          PlayerQueue.addTracks(tracks);
        });
      };
    }
  ]);
