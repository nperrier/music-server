'use strict';

/**
 * @ngdoc directive
 * @name musicApp.directive:albumActionMenu
 * @description
 * # albumActionMenu
 */
angular.module('musicApp').directive('albumActionMenu', [
  '$log',
  '$modal',
  'Album',
  'Playlist',
  'PlayerQueue',
  function(
    $log,
    $modal,
    Album,
    Playlist,
    PlayerQueue
  ) {

    return {
      restrict: 'E',
      templateUrl: '/views/albumActionMenu.html',
      scope: {
        album: '='
        // playlist: '='
      },
      link: function(scope) {

        // Add an Album to the player queue:
        scope.addAlbumToQueue = function(album) {
          $log.debug('Add album to player queue, id: ' + album.id);
          Album.getTracks({ albumId: album.id }, function(tracks) {
            var orderedTracks = _.sortBy(tracks, function(t) { return t.number; });
            PlayerQueue.addTracks(orderedTracks);
          });
        };

/*
 // TODO: Not Yet Implemented!
        scope.editAlbum = function() {

          var modalInstance = $modal.open({
            templateUrl: 'views/editAlbum.html',
            backdrop: false,
            resolve: {
              album: function () {
                return scope.album;
              }
            },
            controller: function ($scope, $modalInstance, album) {

              // private function
              var createAlbumModel = function (album) {
                // TODO: need to consider null artist/album/etc..
                return {
                    name: album.name,
                    artist: album.artist.name,
                    year: album.year
                  };
              };

              $scope.album = createAlbumModel(album);
              // save our original album in order to reset form and check for changes
              $scope.originalAlbum = angular.copy($scope.album);

              $scope.save = function (album) {
                // TODO: Need to add client-side validation
                $modalInstance.close(album);
              };

              $scope.cancel = function () {
                $modalInstance.dismiss('cancelled');
              };

              $scope.reset = function () {
                $scope.album = angular.copy($scope.originalAlbum);
                this.editAlbumForm.$setPristine();
              };

              $scope.isUnchanged = function(album) {
                var isEqual = angular.equals(album, $scope.originalAlbum);
                if (isEqual) {
                  this.editAlbumForm.$setPristine();
                }
                return isEqual;
              };
            }
          });

          modalInstance.result.then(
            function (album) {
              $log.info('TODO: Album updating Not Yet Implemented');
              // $scope.updateTrack($scope.album.id, album);
            },
            function (reason) {
              $log.debug('Modal dismissed: ' + reason);
            }
          );
        };
*/

        // TODO: Pass in via attribute? What happens if the response takes a long time?
        scope.playlists = Playlist.query();

        scope.selectPlaylist = function(album) {

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
            function (playlist) {
              scope.selected = playlist;
              $log.debug('Add album.id: ' + album.id + ' to playlist.id: ' + playlist.id);
              Playlist.addAlbum({ playlistId: playlist.id, albumId: album.id });
            },
            function (reason) {
              $log.debug('Modal dismissed: ' + reason);
            }
          );
        };
      }
    };
  }
]);

