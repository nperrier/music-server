'use strict';

/**
 * @ngdoc service
 * @name musicApp.GenreTrack
 * @description
 * # GenreTrack
 * Factory in the musicApp.
 */
angular.module('musicApp')
  .factory('GenreTrack', ['$resource', function($resource) {

    return $resource('api/genre/:genreId/tracks', {}, {
      get: {
        method: 'GET',
        params: { genreId: '@genreId' },
        isArray: true
      }
    });
  }]);
