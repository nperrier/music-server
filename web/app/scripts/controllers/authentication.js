'use strict';

/**
 * @ngdoc function
 * @name musicApp.controller:AuthenticationCtrl
 * @description
 * # AuthenticationCtrl
 * Controller of the musicApp
 */
angular.module('musicApp').controller('AuthenticationCtrl', [
  '$scope', '$rootScope', '$log', '$state', '$timeout', 'Authentication', 'User',
  function($scope, $rootScope, $log, $state, $timeout, Authentication, User) {

    $scope.authFailed = false;
    $scope.doneLoading = false;

    // Fade the page in:
    $timeout(function() {
      $scope.doneLoading = true;
    }, 10);

    $scope.login = function(username, password) {
      Authentication.login({ username: username, password: password },
      function (response) {
        $log.debug('username: ' + username + ', token: ' + response.token);
        if (response.token) {
          $scope.authFailed = false;
          User.login(username, response.token);
          $state.go('dashboard');
        }
        else if (response.status < 200 || response.status >= 300 ) {
          $scope.authFailed = true;
        }
      });
    };
  }
]);
