'use strict';

/**
 * @ngdoc function
 * @name musicApp.controller:ArtistSearchResultsCtrl
 * @description
 * # ArtistSearchResultsCtrl
 * Controller of the musicApp
 */
angular.module('musicApp').controller('ArtistSearchResultsCtrl', [
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

    $scope.doneLoading = false;
    $scope.artists = [];

    var spinner = new LoadingSpinner($scope);
    spinner.start();

    Search.get({ q: $stateParams.q, table: 'artist' }, function(artists) {
      $scope.artists = artists;
      spinner.checkDoneLoading();
    });
  }
]);
