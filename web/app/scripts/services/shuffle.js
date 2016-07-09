'use strict';

/**
 * @ngdoc service
 * @name musicApp.shuffle
 * @description
 * # shuffle
 * Service in the musicApp.
 */
angular.module('musicApp').factory('Shuffle',
  ['$resource', function($resource) {

    return $resource('api/shuffle', {}, {
      query: {
        method: 'GET',
        isArray: true
      }
    });
  }
]);
