'use strict';

/**
 * @ngdoc function
 * @name musicApp.controller:MainCtrl
 * @description
 * # MainCtrl
 * Controller of the musicApp
 */
angular.module('musicApp').controller('MainCtrl',[
  '$rootScope',
  '$scope',
  '$log',
  '$window',
  '$state',
  'User',
  'AudioPlayer',
  'PlayerQueue',
  'Shuffle',
  'ServerInfo',
  function(
    $rootScope,
    $scope,
    $log,
    $window,
    $state,
    User,
    AudioPlayer,
    PlayerQueue,
    Shuffle,
    ServerInfo
  ) {

    $scope.user = {
      name: User.getUsername(),
      isLoggedIn: User.isLoggedIn()
    };
    $scope.version = ServerInfo.version;
    $scope.searchText = '';

    $scope.search = function() {
      $log.debug('Search clicked: ' + $scope.searchText);
      var opts = {
        reload: true // need this flag in order to search again from the same state
      };
      $state.transitionTo('search-results', { q: $scope.searchText }, opts);
    };

    $scope.shuffle = function() {
      $log.debug('Shuffling tracks');
      Shuffle.query(function(tracks) {
        // on retrieval of tracks, clear queue and add tracks.
        // Player should start automatically
        PlayerQueue.clear();
        PlayerQueue.addTracks(tracks);
      });
    };

    $scope.logout = function() {
      User.logout();
      $state.go('authentication');
      $rootScope.$emit('authenticated', false);
    };
  }]
);
