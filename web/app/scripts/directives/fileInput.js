'use strict';

angular.module('musicApp').directive('fileInput', [
  function() {
    return {
      restrict: 'A',
      scope: {
        fileInput: '=',
        filePreview: '='
      },
      link: function(scope, element) {
        element.bind('change', function(changeEvent) {
          scope.fileInput = changeEvent.target.files[0];

          var reader = new FileReader();
          reader.onload = function(loadEvent) {
            scope.$apply(function() {
              scope.filePreview = loadEvent.target.result;
            });
          };

          reader.readAsDataURL(scope.fileInput);
        });
      }
    };
  }
]);
