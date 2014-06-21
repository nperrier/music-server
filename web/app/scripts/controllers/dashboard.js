'use strict';

/**
 * @ngdoc function
 * @name musicApp.controller:DashboardCtrl
 * @description
 * # DashboardCtrl
 * Controller of the musicApp
 */
angular.module('musicApp')
  .controller('DashboardCtrl', ['$scope', function($scope) {
	$scope.message = 'Dashboard';
}]);
