'use strict';

/**
 * @ngdoc service
 * @name musicApp.LibraryScan
 * @description
 * # Scan a Library
 * Factory in the musicApp.
 */
angular.module('musicApp')
  .factory('LibraryScan', ['$resource', function($resource) {

    return $resource('api/libraryscan', { libraryId: '@libraryId' }, {
      // Scan the library
      scan: {
        method: 'POST'
      }
    });
  }]);
