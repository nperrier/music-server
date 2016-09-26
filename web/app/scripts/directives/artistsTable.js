'use strict';

angular.module('musicApp').directive('artistsTable', [
  function() {

    return {
      restrict: 'E',
      scope: {
        artists: '='
      },
      templateUrl: '/views/artistsTable.html',
      link: function(scope, element, attrs) {
        scope.sortField = 'name';
        scope.reverse = false;
      }
    };
  }
]);
