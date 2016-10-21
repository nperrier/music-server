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
      update: {
        method: 'PUT',
        params: { playlist: '@playlist' }
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
        url: 'api/playlist/:playlistId/album/:albumId',
        method: 'POST',
        params: { playlistId: '@playlistId', albumId: '@albumId' }
      },
      updateTracks: {
        url: 'api/playlist/:playlistId/tracks',
        method: 'PUT',
        isArray: true,
        params: { playlistId: '@playlistId' }
      },
      deleteTrack: {
        url: 'api/playlist/:playlistId/tracks/:playlistTrackId',
        method: 'DELETE',
        isArray: true,
        params: { playlistId: '@playlistId', playlistTrackId: '@playlistTrackId' }
      }
    });
  }
]);
