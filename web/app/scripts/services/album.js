'use strict';

/**
 * @ngdoc service
 * @name musicApp.Album
 * @description
 * # Album
 * Factory in the musicApp.
 */
angular.module('musicApp').factory('Album', [
  '$resource', function($resource) {

    return $resource('api/album/:albumId', {}, {
      query: {
        method: 'GET',
        isArray: true
      },
      get: {
        method: 'GET',
        params: { albumId: '@albumId' }
      },
      getTracks: {
        url: 'api/album/:albumId/tracks',
        method: 'GET',
        params: { albumId: '@albumId' },
        isArray: true
      }
    });
  }
]);
