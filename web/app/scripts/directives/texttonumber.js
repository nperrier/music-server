'use strict';

/**
 * @ngdoc directive
 * @name musicApp.directive:texttonumber
 * @description
 * # texttonumber
 */
angular.module('musicApp')
  .directive('texttonumber', function () {
    return {
      restrict: 'A',
      require: 'ngModel',
      link: function (scope, element, attrs, ctrl) {
        ctrl.$parsers.push(function (value) {
          // var parsed = Number(value);
          // return !isNaN(parsed) ? parsed: value;
          // return _.isNumber(value) ? parseInt(value, 10) : value;
          if (value === null) {
            return null;
          }

          return value.match(/^\d+$/) ? parseInt(value, 10) : value;
        });
      }
    };
  });
