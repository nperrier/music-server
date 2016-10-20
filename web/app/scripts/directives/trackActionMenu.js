'use strict';

/**
 * @ngdoc directive
 * @name musicApp.directive:trackActionMenu
 * @description
 * # trackActionMenu
 */
angular.module('musicApp').directive('trackActionMenu', [
  '$log',
  '$modal',
  'User',
  'Track',
  'Playlist',
  'PlayerQueue',
  function(
    $log,
    $modal,
    User,
    Track,
    Playlist,
    PlayerQueue
  ) {

    return {
      restrict: 'E',
      templateUrl: '/views/trackActionMenu.html',
      scope: {
        track: '=',
        playlists: '='
      },
      link: function(scope) {

        scope.updateTrack = function(trackId, trackInfo) {
          $log.debug('updateTrack, trackId: ' + trackId);
          Track.update({ trackId: trackId }, trackInfo, function () {
            // do something after updating
          });
        };

        scope.addTrackToQueue = function(track) {
          PlayerQueue.addTrack(track);
          $log.debug('Added track to player queue, track.id: ' + track.id);
        };

        scope.addTrackToPlaylist = function(track, playlist) {
          Playlist.addTracks({ playlistId: playlist.id }, [ track.id ]);
          $log.debug('Added track.id: ' + track.id + ' to playlist.id: ' + playlist.id);
        };

        scope.editTrack = function() {

          var modalInstance = $modal.open({
            templateUrl: 'views/editTrack.html',
            backdrop: false,
            resolve: {
              track: function() {
                return scope.track;
              }
            },
            controller: function($scope, $modalInstance, track) {

              // private
              var createTrackModel = function(track) {
                // TODO: need to consider null artist/album/etc..
                return {
                    name: track.name,
                    artist: track.artist.name,
                    album: track.album.name,
                    genre: track.genre.name,
                    year: track.year,
                    number: track.number,
                    coverArtUrl: track.coverArtUrl
                  };
              };

              $scope.track = createTrackModel(track);
              // save our original track in order to reset form and check for changes
              $scope.originalTrack = angular.copy($scope.track);

              $scope.save = function(track) {
                // TODO: Need to add client-side validation
                $modalInstance.close(track);
              };

              $scope.cancel = function() {
                $modalInstance.dismiss('cancelled');
              };

              $scope.reset = function() {
                $scope.track = angular.copy($scope.originalTrack);
                this.editTrackForm.$setPristine();
              };

              $scope.isUnchanged = function(track) {
                var isEqual = angular.equals(track, $scope.originalTrack);
                if (isEqual) {
                  this.editTrackForm.$setPristine();
                }
                return isEqual;
              };
            }
          });

          modalInstance.result.then(
            function(track) {
              scope.updateTrack(scope.track.id, track);
            },
            function(reason) {
              $log.debug('Modal dismissed: ' + reason);
            }
          );
        };

        scope.selectPlaylist = function(track) {

          var modalInstance = $modal.open({
            templateUrl: 'views/playlistsModal.html',
            size: 'sm',
            backdrop: false,
            resolve: {
              playlists: function () {
                return scope.playlists;
              }
            },
            controller: function($scope, $modalInstance, playlists) {
              $scope.playlists = playlists;

              $scope.selected = {
                playlist: scope.playlists[0]
              };

              $scope.ok = function() {
                $modalInstance.close($scope.selected.playlist);
              };

              $scope.cancel = function() {
                $modalInstance.dismiss('cancelled');
              };
            }
          });

          modalInstance.result.then(
            function(playlist) {
              scope.selected = playlist;
              scope.addTrackToPlaylist(track, playlist);
            },
            function(reason) {
              $log.debug('Modal dismissed: ' + reason);
            }
          );
        };
      }
    };
  }]);
