angular.module('musicApp').service('EditTrack', [
  '$log',
  '$uibModal',
  function(
    $log,
    $uibModal
  ) {

    this.openModal = function openModal(track) {
      var modalInstance = $uibModal.open({
        templateUrl: 'views/editTrack.html',
        backdrop: false,
        resolve: {
          track: function() {
            return track;
          }
        },
        controller: function($scope, $uibModalInstance, track) {
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
            $uibModalInstance.close(track);
          };

          $scope.cancel = function() {
            $uibModalInstance.dismiss('cancelled');
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

      return modalInstance.result;
    };
  }
]);
