'use strict';

angular.module('musicApp').directive('tracksTable', [
  '$log',
  'PlayerQueue',
  function(
    $log,
    PlayerQueue
  ) {

    return {
      restrict: 'E',
      scope: {
        tracks: '=',
        playlists: '=',
        onChange: '&'
      },
      templateUrl: '/views/tracksTable.html',
      link: function(scope) {

        scope.sortField = 'name';
        scope.reverse = false;

        scope.playTrack = function(track) {
          $log.debug('Add track to player queue, id: ' + track.id);
          PlayerQueue.playTrackNow(track);
        };
      }
    };
  }
]);
