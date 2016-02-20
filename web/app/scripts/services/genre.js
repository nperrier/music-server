'use strict';

/**
 * @ngdoc service
 * @name musicApp.Genre
 * @description
 * # Genre
 * Factory in the musicApp.
 */
angular.module('musicApp').factory('Genre', [
  '$resource', function($resource) {

    return $resource('api/genre', {}, {
      query: {
        method: 'GET',
        isArray: true
      },
    });
  }
]);
