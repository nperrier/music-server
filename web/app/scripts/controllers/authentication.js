'use strict';

/**
 * @ngdoc function
 * @name musicApp.controller:AuthenticationCtrl
 * @description
 * # AuthenticationCtrl
 * Controller of the musicApp
 */
angular.module('musicApp').controller('AuthenticationCtrl', [
  '$scope', '$rootScope', '$log', '$state', 'Authentication', 'store',
  function($scope, $rootScope, $log, $state, Authentication, store) {

    $scope.authFailed = false;

    $scope.login = function(username, password) {
      Authentication.login({ username: username, password: password },
      function (response) {
        console.log('username: ' + username + ', token: ' + response.token);
        if (response.token) {
          $scope.authFailed = false;
          $rootScope.currentUser = username;
          // store token:
          store.set('auth-token', response.token);
          $state.go('dashboard');
        }
        else if (response.status === 401) {
          $scope.authFailed = true;
        }
      });
    };
  }
]);
