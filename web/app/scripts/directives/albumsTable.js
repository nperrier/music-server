'use strict';

angular.module('musicApp').directive('albumsTable', [
  function() {

    return {
      restrict: 'E',
      scope: {
        artist: '=',
        albums: '='
      },
      templateUrl: '/views/albumsTable.html',
      link: function(scope, element, attrs) {
        scope.sortField = 'name';
        scope.reverse = false;
      }
    };
  }
]);
