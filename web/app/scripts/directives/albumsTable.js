'use strict';

angular.module('musicApp').directive('albumsTable', [
  '$log',
  '_',
  'Album',
  'PlayerQueue',
  function(
    $log,
    _,
    Album,
    PlayerQueue
  ) {

    return {
      restrict: 'E',
      scope: {
        albums: '=',
        playlists: '='
      },
      templateUrl: '/views/albumsTable.html',
      link: function(scope, element, attrs) {

        scope.sortField = 'name';
        scope.reverse = false;

        scope.playAlbum = function(album) {
          $log.debug('Add album to player queue, id: ' + album.id);
          Album.getTracks({ albumId: album.id }, function(tracks) {
            var orderedTracks = _.sortBy(tracks, function(t) { return t.number; });
            PlayerQueue.playTracksNow(orderedTracks);
          });
        };
      }
    };
  }
]);
