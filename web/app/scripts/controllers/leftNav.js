'use strict';

/**
 * @ngdoc function
 * @name musicApp.controller:LeftNavCtrl
 * @description
 * # LeftNavCtrl
 * Controller of the musicApp
 */
angular.module('musicApp').controller('LeftNavCtrl', [
  '$scope',
  '$location',
  function(
    $scope,
    $location
  ) {

  	// set the active nav pill
  	$scope.isActive = function (viewLocation) {
  		return viewLocation === $location.path();
  	};
  }
]);
