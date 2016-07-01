'use strict';

/**
 * @ngdoc function
 * @name musicApp.controller:MainCtrl
 * @description
 * # MainCtrl
 * Controller of the musicApp
 */
angular.module('musicApp').controller('MainCtrl', function($scope, $state, User) {

  $scope.user = {
    name: User.getUsername(),
    isLoggedIn: User.isLoggedIn()
  };

  $scope.logout = function() {
    User.logout();
    $state.go('authentication');
  }

});
