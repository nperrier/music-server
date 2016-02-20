'use strict';

/**
 * @ngdoc service
 * @name musicApp.Library
 * @description
 * # Library
 * Factory in the musicApp.
 */
angular.module('musicApp').factory('Library', [
  '$resource', function($resource) {

    return $resource('api/library/:libraryId', { libraryId: '@libraryId' }, {
      query: {
        method: 'GET',
        isArray: true
      },
      get: {
        method: 'GET' //,
        //params: { libraryId: '@libraryId' }
      },
      save: {
        method: 'POST',
        params: { path: '@path' }
      },
      // Scan the library
      scan: {
        url: 'api/library/:libraryId/scan',
        method: 'POST', // TODO: PUT?
        //transformRequest: function(body, headersGetter) {
        //  return []; // this sets the 'libraryId' param in the body...REMOVE IT!
      }
    });
  }
]);
