'use strict';

/**
 * @ngdoc function
 * @name musicApp.controller:AlbumDetailCtrl
 * @description
 * # AlbumDetailCtrl
 * Controller of the musicApp
 */
angular.module('musicApp')
  .controller('AlbumDetailCtrl', ['$scope', '$routeParams', '$log', 'Album', 'AlbumTrack', 'Track', 'Playlist', 'PlayerQueue',
    function($scope, $routeParams, $log, Album, AlbumTrack, Track, Playlist, PlayerQueue) {

	$scope.sortField = 'number';
	$scope.reverse = false;

  $scope.playlists = Playlist.query();

	Album.get({ albumId: $routeParams.albumId }, function(album) {
		$scope.album = album;
	});

	AlbumTrack.get({ albumId: $routeParams.albumId }, function(tracks) {
		$scope.tracks = tracks;
	});

  $scope.updateTrack = function(trackId, trackInfo) {
    $log.info('updateTrack, trackId: ' + trackId);
    Track.update({ trackId: trackId }, trackInfo, function (track) {
      // do something after updating
    });
  };

  $scope.addTrackToPlaylist = function(track, playlist) {
    Playlist.addTracks({ playlistId: playlist.id }, [ track.id ]);
    $log.info('Added track.id: ' + track.id + ' to playlist.id: ' + playlist.id);
  };

  $scope.addTrackToQueue = function(track) {
    PlayerQueue.addTrack(track);
    $log.info('Added track to player queue, track.id: ' + track.id);
  };

}]);
