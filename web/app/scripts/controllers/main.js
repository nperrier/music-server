'use strict';

/**
 * @ngdoc function
 * @name musicApp.controller:MainCtrl
 * @description
 * # MainCtrl
 * Controller of the musicApp
 */
angular.module('musicApp').controller('MainCtrl',[
  '$rootScope', '$scope', '$log', '$window', '$state',
  'User', 'AudioPlayer', 'PlayerQueue', 'Shuffle',
  function($rootScope, $scope, $log, $window, $state,
    User, AudioPlayer, PlayerQueue, Shuffle) {

    $scope.user = {
      name: User.getUsername(),
      isLoggedIn: User.isLoggedIn()
    };

    $scope.version = '0.4.0';

    $scope.shuffle = function() {
      $log.info("Shuffling tracks");
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
