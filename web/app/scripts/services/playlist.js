'use strict';

/**
 * @ngdoc service
 * @name musicApp.Playlist
 * @description
 * # Playlist
 * Factory in the musicApp.
 */
angular.module('musicApp')
  .factory('Playlist', ['$resource', function($resource) {
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
      }
    });
  }]);
