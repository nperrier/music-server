'use strict';

/**
 * @ngdoc function
 * @name musicApp.controller:TracksCtrl
 * @description
 * # TracksCtrl
 * Controller of the musicApp
 */
angular.module('musicApp')
  .controller('TracksCtrl', ['$scope', 'Track', function($scope, Track) {

  $scope.tracks = Track.query();

  $scope.sortField = 'name';
  $scope.reverse = true;

}]);
