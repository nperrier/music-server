'use strict';

/**
 * @ngdoc directive
 * @name musicApp.directive:playlistActionMenu
 * @description
 * # playlistActionMenu
 */
angular.module('musicApp')
  .directive('playlistActionMenu', ['$log', '$modal',
    function($log, $modal) {

    return {
      restrict: 'E',
      templateUrl: '/views/playlistactionmenu.html',
      // inherits scope from parent:
      scope: false,
      controller: function ($scope, $element) {

        $scope.delete = function () {

          var modalInstance = $modal.open({
            templateUrl: 'views/playlistdeletemodal.html',
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
                $log.info('Remove playlist, id: ' + playlist.id);
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
              $log.info('Modal dismissed');
            }
          );
        };
      }
    };
  }]);
