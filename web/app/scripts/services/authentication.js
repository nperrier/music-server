'use strict';

/**
 * @ngdoc service
 * @name musicApp.Authentication
 * @description
 * # Authentication
 * Service in the musicApp.
 */
angular.module('musicApp').factory('Authentication', [
  '$resource', function($resource) {

    return $resource('api/authentication', {}, {
      login: {
        method: 'POST',
        params: {
          username: '@username',
          password: '@password'
        }
      }
    });
  }
]);
