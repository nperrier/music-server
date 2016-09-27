'use strict';

/**
 * @ngdoc directive
 * @name musicApp.directive:textToNumber
 * @description
 * # textToNumber
 */
angular.module('musicApp').directive('textToNumber',
  function() {

    return {
      restrict: 'A',
      require: 'ngModel',
      link: function (scope, element, attrs, ctrl) {
        ctrl.$parsers.push(function (value) {
          if (value === null) {
            return null;
          }
          return value.match(/^\d+$/) ? parseInt(value, 10) : value;
        });
      }
    };
  }
);
