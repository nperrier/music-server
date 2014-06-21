'use strict';

/**
 * @ngdoc service
 * @name musicApp.AlbumTrack
 * @description
 * # AlbumTrack
 * Factory in the musicApp.
 */
angular.module('musicApp')
  .factory('AlbumTrack', ['$resource', function($resource) {
  
    return $resource('api/album/:albumId/tracks', {}, {
      get: {
        method: 'GET',
        params: { albumId: '@albumId' },
        isArray: true
      }
    });
}]);