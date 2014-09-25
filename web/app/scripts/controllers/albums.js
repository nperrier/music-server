'use strict';

/**
 * @ngdoc function
 * @name musicApp.controller:AlbumsCtrl
 * @description
 * # AlbumsCtrl
 * Controller of the musicApp
 */
angular.module('musicApp')
  .controller('AlbumsCtrl', ['$scope', 'Album', function($scope, Album) {

    $scope.sortField = 'name';
    $scope.reverse = true;
    $scope.doneLoading = false;

	  $scope.albums = Album.query(function () {
      $scope.doneLoading = true;
    });
  }]);

