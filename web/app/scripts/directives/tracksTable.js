'use strict';

angular.module('musicApp').directive('tracksTable', [
  function() {

    return {
      restrict: 'E',
      scope: {
        tracks: '='
      },
      templateUrl: '/views/tracksTable.html',
      link: function(scope, element, attrs) {
        scope.sortField = 'name';
        scope.reverse = false;
      }
    };
  }
]);
