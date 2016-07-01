'use strict';

/**
 * @ngdoc service
 * @name musicApp.User
 * @description
 * # User
 * Service in the musicApp.
 */
angular.module('musicApp').service('User', ['store', function (store) {

    var self = this;

    self.login = function login(username, token) {
      if (!angular.isDefined(username)) {
        throw new Error('Missing username!');
      }
      if (!angular.isDefined(token)) {
        throw new Error('Missing token!');
      }

      store.set('username', username);
      store.set('auth-token', token);
    };

    self.logout = function logout() {
      store.remove('username');
      store.remove('auth-token');
    };

    self.isLoggedIn = function isLoggedIn() {
      return !!(store.get('username') && store.get('auth-token'));
    };

    self.getToken = function getToken() {
      return store.get('auth-token');
    };

    self.getUsername = function getUsername() {
      return store.get('username');
    };
  }
]);
