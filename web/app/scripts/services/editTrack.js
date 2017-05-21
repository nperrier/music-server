'use strict';

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

          // TODO: handle resetting cover image
          scope.coverImage = $scope.track.coverArtUrl;
          // to track changing the cover art image:
          $scope.newCover = {
            file: null
          };

          $scope.save = function(track, newCover) {
            // TODO: Need to add client-side validation
            $uibModalInstance.close({track: track, image: newCover.file});
          };

          $scope.cancel = function() {
            $uibModalInstance.dismiss('cancelled');
          };

          $scope.reset = function() {
            $scope.track = angular.copy($scope.originalTrack);
            $scope.coverImage = $scope.track.coverArtUrl;
            $scope.newCover.file = null;

            this.editTrackForm.$setPristine();
          };

          $scope.isUnchanged = function(track, newCover) {
            var trackEqual = angular.equals(track, $scope.originalTrack);
            var coverEqual = (newCover.file === null);
            var unchanged = (trackEqual && coverEqual);
            if (unchanged) {
              this.editTrackForm.$setPristine();
            }
            return unchanged;
          };
        }
      });

      return modalInstance.result;
    };
  }
]);
