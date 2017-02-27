'use strict';

/**
 * @ngdoc directive
 * @name musicApp.directive:trackActionMenu
 * @description
 * # trackActionMenu
 */
angular.module('musicApp').directive('trackActionMenu', [
  '$log',
  '$uibModal',
  'User',
  'Track',
  'Playlist',
  'PlayerQueue',
  'EditTrack',
  'SelectPlaylist',
  'CoverArtService',
  function(
    $log,
    $uibModal,
    User,
    Track,
    Playlist,
    PlayerQueue,
    EditTrack,
    SelectPlaylist,
    CoverArtService
  ) {

    return {
      restrict: 'E',
      templateUrl: '/views/trackActionMenu.html',
      scope: {
        track: '=',
        playlists: '=',
        onChange: '&'
      },
      link: function(scope) {

        scope.updateTrack = function(trackId, trackInfo) {
          $log.debug('updateTrack', trackId);
          Track.update({ trackId: trackId }, trackInfo, function () {
            scope.onChange();
          },
          function (error) {
            $log.error('Unable to update track', trackId, error);
          });
        };

        scope.addTrackToQueue = function(track) {
          PlayerQueue.addTrack(track);
          $log.debug('Added track to player queue, track.id: ', track.id);
        };

        scope.addTrackToPlaylist = function(track, playlist) {
          Playlist.addTracks({ playlistId: playlist.id }, [ track.id ]);
          $log.debug('Added track to playlist', track.id, playlist.id);
        };

        scope.editTrack = function editTrack() {

          var modalPromise = EditTrack.openModal(scope.track);

          modalPromise.then(function(modalResult) {
            var newCoverImage = modalResult.image;
            var newTrack = modalResult.track;

            var coverUploadPromise = CoverArtService.upload(newCoverImage, scope.track.id).then(function(result) {
              newTrack.coverArtUrl = result.data.coverUrl;
              newTrack.coverHash = result.data.coverHash;
              newTrack.coverStorageKey = result.data.coverKey;
              var trackUpdatePromise = Track.update({ trackId: scope.track.id }, newTrack).$promise;
              return trackUpdatePromise;
            }).then(function() {
              scope.onChange();
            });

            return coverUploadPromise;
          },
          function(reason) {
            $log.debug('Modal dismissed', reason);
          }).catch(function(err) {
            $log.error(err);
          });
        };

        scope.selectPlaylist = function selectPlaylist(track) {
          var promise = SelectPlaylist.openModal(scope.playlists);
          promise.then(
            function(playlist) {
              scope.selected = playlist;
              scope.addTrackToPlaylist(track, playlist);
            },
            function(reason) {
              $log.debug('Modal dismissed, reason:', reason);
            }
          );
        };
      }
    };
  }]);
