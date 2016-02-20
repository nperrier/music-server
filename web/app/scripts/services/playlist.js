'use strict';

/**
 * @ngdoc service
 * @name musicApp.Playlist
 * @description
 * # Playlist
 * Factory in the musicApp.
 */
angular.module('musicApp').factory('Playlist', [
  '$resource', function($resource) {

    return $resource('api/playlist/:playlistId', {}, {
      query: {
        method: 'GET',
        isArray: true
      },
      get: {
        method: 'GET',
        params: { playlistId: '@playlistId' }
      },
      save: {
        method: 'POST',
        params: { name: '@name' }
      },
      delete: {
        method: 'DELETE',
        params: { playlistId: '@playlistId' }
      },
      getTracks: {
        url: 'api/playlist/:playlistId/tracks',
        method: 'GET',
        isArray: true,
        params: { playlistId: '@playlistId' }
      },
      addTracks: {
        url: 'api/playlist/:playlistId/tracks',
        method: 'POST',
        params: { tracks: '@tracks' }
      },
      addAlbum: {
        url: 'api/playlist/:playlistId/album',
        method: 'POST',
        params: { albumId: '@albumId' }
      }
    });
  }
]);
