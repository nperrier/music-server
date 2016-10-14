'use strict';

/**
 * @ngdoc function
 * @name musicApp.controller:AlbumSearchResultsCtrl
 * @description
 * # AlbumSearchResultsCtrl
 * Controller of the musicApp
 */
angular.module('musicApp').controller('AlbumSearchResultsCtrl', [
  '$scope',
  '$log',
  '$timeout',
  '$stateParams',
  'LoadingSpinner',
  'Search',
  function(
    $scope,
    $log,
    $timeout,
    $stateParams,
    LoadingSpinner,
    Search
  ) {

    $scope.albums = [];

    var spinner = new LoadingSpinner($scope, 1);
    spinner.start();

    Search.get({ q: $stateParams.q, table: 'album' }, function(results) {
      $scope.albums = results.albums;
      spinner.checkDoneLoading();
    });
  }
]);
