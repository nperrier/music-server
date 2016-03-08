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
              },
              index: function() {
                return $scope.$index;
              }
            },
            controller: function ($scope, $modalInstance, playlist, index) {

              $scope.playlist = playlist;

              $scope.ok = function (playlist) {
                $log.debug('Remove playlist, id: ' + playlist.id);
                $modalInstance.close(playlist, index);
              };

              $scope.cancel = function () {
                $modalInstance.dismiss();
              };
            }
          });

          modalInstance.result.then(
            function (playlist, index) {
              $scope.deletePlaylist(playlist, index);
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
