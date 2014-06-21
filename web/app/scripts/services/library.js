'use strict';

/**
 * @ngdoc service
 * @name musicApp.Library
 * @description
 * # Library
 * Factory in the musicApp.
 */
angular.module('musicApp')
  .factory('Library', ['$resource', function($resource) {
  
    return $resource('api/library/:libraryId', {}, {
      query: {
        method: 'GET',
        isArray: true
      },
      get: {
        method: 'GET',
        params: { libraryId: '@libraryId' },
        isArray: false
      },
      save: {
        method: 'POST',
        params: { path: '@path' }
      }
    });
  }]);