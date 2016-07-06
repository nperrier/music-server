'use strict';

/**
 * @ngdoc function
 * @name musicApp.controller:MainCtrl
 * @description
 * # MainCtrl
 * Controller of the musicApp
 */
angular.module('musicApp').controller('MainCtrl',
  ['$rootScope', '$scope', '$window', '$state', 'User',
  function($rootScope, $scope, $window, $state, User) {

    $scope.user = {
      name: User.getUsername(),
      isLoggedIn: User.isLoggedIn()
    };

    $scope.version = '0.4.0';

    $scope.logout = function() {
      User.logout();
      $state.go('authentication');
      $rootScope.$emit('authenticated', false);
    };
  }]
);
