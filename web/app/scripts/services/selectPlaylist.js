angular.module('musicApp').service('SelectPlaylist', [
  '$log',
  '$uibModal',
  function(
    $log,
    $uibModal
  ) {

    this.openModal = function openModal(playlists) {
      var modalInstance = $uibModal.open({
        templateUrl: 'views/playlistsModal.html',
        size: 'sm',
        backdrop: false,
        resolve: {
          playlists: function () {
            return playlists;
          }
        },
        controller: function($scope, $uibModalInstance, playlists) {
          $scope.playlists = playlists;

          $scope.selected = {
            playlist: playlists[0]
          };

          $scope.ok = function() {
            $uibModalInstance.close($scope.selected.playlist);
          };

          $scope.cancel = function() {
            $uibModalInstance.dismiss('cancelled');
          };
        }
      });

      return modalInstance.result;
    };
  }
]);
