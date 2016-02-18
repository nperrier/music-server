'use strict';

/**
 * @ngdoc directive
 * @name musicApp.directive:fadeIn
 * @description
 * # fadeIn
 */
angular.module('musicApp')
  .directive('fadeIn', function () {
    return {
      restrict: 'A',
      link: function($scope, $element) {
        $element.addClass('ng-hide-remove');
        $element.on('load', function() {
          $element.addClass('ng-hide-add');
        });
      }
    };
  });
