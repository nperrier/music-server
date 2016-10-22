'use strict';

/**
 * @ngdoc function
 * @name musicApp.controller:AuthenticationCtrl
 * @description
 * # AuthenticationCtrl
 * Controller of the musicApp
 */
angular.module('musicApp').controller('AuthenticationCtrl', [
  '$scope',
  '$rootScope',
  '$log',
  '$state',
  '$timeout',
  'Authentication',
  'User',
  function(
    $scope,
    $rootScope,
    $log,
    $state,
    $timeout,
    Authentication,
    User
  ) {

    $scope.authFailed = false;
    $scope.doneLoading = false;

    // Fade the page in:
    $timeout(function() {
      $scope.doneLoading = true;
    }, 10);

    $scope.login = function(username, password) {

      $scope.authFailed = false;

      // Prevent flickering to give user some feedback if authentication fails
      // more than once
      $timeout(function() {
        Authentication.login({ username: username, password: password },
          function (response) {
            $log.debug('username: ' + username + ', token: ' + response.token);
            if (response.token) {
              $scope.authFailed = false;
              User.login(username, response.token);
              $state.go('dashboard');
              $rootScope.$emit('authenticated', true);
            }
          },
          function (error) {
            $scope.authFailed = true;
          }
        );
      }, 200);
    };
  }
]);
