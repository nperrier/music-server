'use strict';

/**
 * @ngdoc directive
 * @name musicApp.directive:emptyToNull
 * @description
 * # emptyToNull
 */
angular.module('musicApp').directive('emptyToNull',
  function() {

    return {
      restrict: 'A',
      require: 'ngModel',
      link: function (scope, element, attrs, ctrl) {
        ctrl.$parsers.push(function (value) {
          return value === '' ? null : value;
        });
      }
    };
  }
);
