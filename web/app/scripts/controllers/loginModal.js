'use strict';

/**
 * @ngdoc function
 * @name musicApp.controller:LoginModalCtrl
 * @description
 * # LoginModalCtrl
 * Controller of the musicApp
 */
angular.module('musicApp').controller('LoginModalCtrl', [
  '$scope', 'Authentication', function ($scope, Authentication) {

    $scope.authFailed = false;

    this.cancel = $scope.$dismiss;

    this.login = function(username, password) {
      Authentication.login({ username: username, password: password },
      function (response) {
        console.log('username: ' + username + ', token: ' + response.token);
        if (response.token) {
          $scope.authFailed = false;
          $scope.$close(username);
        }
        else if (response.status === 401) {
          $scope.authFailed = true;
        }
      });
    };
  }
]);
