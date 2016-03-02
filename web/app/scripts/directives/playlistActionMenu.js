'use strict';

/**
 * @ngdoc directive
 * @name musicApp.directive:playlistActionMenu
 * @description
 * # playlistActionMenu
 */
angular.module('musicApp').directive('playlistActionMenu', [
  '$log', '$modal', function($log, $modal) {

    return {
      restrict: 'E',
      templateUrl: '/views/playlistActionMenu.html',
      // inherits scope from parent:
      scope: false,
      controller: function ($scope) {

        $scope.delete = function () {
          var modalInstance = $modal.open({
            templateUrl: 'views/playlistDeleteModal.html',
            size: 'sm',
            backdrop: false,
            resolve: {
              playlist: function () {
                return $scope.p;
              }
            },
            controller: function ($scope, $modalInstance, playlist) {

              $scope.playlist = playlist;

              $scope.ok = function (playlist) {
                $log.debug('Remove playlist, id: ' + playlist.id);
                $modalInstance.close(playlist);
              };

              $scope.cancel = function () {
                $modalInstance.dismiss();
              };
            }
          });

          modalInstance.result.then(
            function (playlist) {
              $scope.deletePlaylist(playlist);
            },
            function () {
              $log.debug('Modal dismissed');
            }
          );
        };
      }
    };
  }
]);