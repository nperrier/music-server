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
  '_',
  'Album',
  'Playlist',
  'PlayerQueue',
  'User',
  function(
    $log,
    $modal,
    _,
    Album,
    Playlist,
    PlayerQueue,
    User
  ) {

    return {
      restrict: 'E',
      templateUrl: '/views/albumActionMenu.html',
      scope: {
        album: '=',
        playlists: '='
      },
      link: function(scope) {

        var buildDownloadURL = function(albumId) {
          var url = '/api/album/download/' + albumId;
          url += '?token=' + User.getToken();
          return url;
        };

        scope.album.downloadURL = buildDownloadURL(scope.album.id);

        scope.updateAlbum = function(albumId, albumInfo) {
          $log.debug('updateAlbum, albumId: ' + albumId);
          Album.update({ albumId: albumId }, albumInfo, function () {
            // do something after updating
          });
        };

        // Add an Album to the player queue:
        scope.addAlbumToQueue = function(album) {
          $log.debug('Add album to player queue, id: ' + album.id);
          Album.getTracks({ albumId: album.id }, function(tracks) {
            var orderedTracks = _.sortBy(tracks, function(t) { return t.number; });
            PlayerQueue.addTracks(orderedTracks);
          });
        };

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

              var createAlbumModel = function (album) {
                // TODO: need to consider null artist/album/etc..
                return {
                    name: album.name,
                    artist: album.artist.name,
                    //year: album.year,
                    coverArtUrl: album.coverArtUrl
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
              scope.updateAlbum(scope.album.id, album);
            },
            function (reason) {
              $log.debug('Modal dismissed: ' + reason);
            }
          );
        };

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

