'use strict';

/**
 * @ngdoc directive
 * @name musicApp.directive:albumActionMenu
 * @description
 * # albumActionMenu
 */
angular.module('musicApp')
  .directive('albumActionMenu', ['$log', '$modal', function($log, $modal) {

    return {
      restrict: 'E',
      templateUrl: '/views/albumactionmenu.html',
      scope: false, // inherit from parent scope - assumes 'album' is in scope
      controller: function ($scope, $element) {

        $scope.editAlbum = function(album) {

          var modalInstance = $modal.open({
            templateUrl: 'views/editalbum.html',
            backdrop: false,
            resolve: {
              album: function () {
                return $scope.album;
              }
            },
            controller: function ($scope, $modalInstance, album) {

              // private
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
              $scope.updateTrack($scope.album.id, album);
            },
            function (reason) {
              $log.info('Modal dismissed: ' + reason);
            }
          );
        };

        // add to queue moved to album controller

        $scope.selectPlaylist = function (album) {

          var modalInstance = $modal.open({
            templateUrl: 'views/playlistsmodal.html',
            size: 'sm',
            backdrop: false,
            resolve: {
              playlists: function () {
                return $scope.playlists;
              }
            },
            controller: function ($scope, $modalInstance, playlists) {
              $scope.playlists = playlists;

              $scope.selected = {
                playlist: $scope.playlists[0]
              };

              $scope.ok = function () {
                $modalInstance.close($scope.selected.playlist);
              };

              $scope.cancel = function () {
                $modalInstance.dismiss('cancelled');
              };
            }
          });

          modalInstance.result.then(
            function (playlist) {
              $scope.selected = playlist;
              $scope.addAlbumToPlaylist(album, playlist);
            },
            function (reason) {
              $log.info('Modal dismissed: ' + reason);
            }
          );
        };
      }
    };
  }
]);

