'use strict';

/**
 * @ngdoc service
 * @name musicApp.Track
 * @description
 * # Track
 * Factory in the musicApp.
 */
angular.module('musicApp')
  .factory('Track', ['$resource', function($resource) {

    return $resource('api/track/:trackId', {}, {
      query: {
        method: 'GET',
        isArray: true
      },
      get: {
        method: 'GET',
        params: { trackId: '@trackId' }
      }
    });
  }]);
