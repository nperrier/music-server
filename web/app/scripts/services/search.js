'use strict';

/**
 * @ngdoc service
 * @name musicApp.search
 * @description
 * # search
 * Factory in the musicApp.
 */
angular.module('musicApp').factory('Search', [
  '$resource', function($resource) {

    return $resource('api/search/:table', {}, {
      query: {
        method: 'GET',
        isArray: false
      },
      get: {
        method: 'GET',
        params: { table: '@table' }
      },
    });
  }
]);
