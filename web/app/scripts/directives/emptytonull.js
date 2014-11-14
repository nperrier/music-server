'use strict';

/**
 * @ngdoc directive
 * @name musicApp.directive:emptytonull
 * @description
 * # emptytonull
 */
angular.module('musicApp')
  .directive('emptytonull', function () {
    return {
      restrict: 'A',
      require: 'ngModel',
      link: function (scope, element, attrs, ctrl) {
        ctrl.$parsers.push(function (value) {
          return value === '' ? null : value;
        });
      }
    };
  });
