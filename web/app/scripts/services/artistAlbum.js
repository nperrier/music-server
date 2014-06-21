'use strict';

/**
 * @ngdoc service
 * @name musicApp.ArtistAlbum
 * @description
 * # ArtistAlbum
 * Factory in the musicApp.
 */
angular.module('musicApp')
  .factory('ArtistAlbum', ['$resource', function($resource) {
  
    return $resource('api/artist/:artistId/albums', {}, {
      get: {
        method: 'GET',
        params: { artistId: '@artistId' },
        isArray: true
      }
    });
  }]);
