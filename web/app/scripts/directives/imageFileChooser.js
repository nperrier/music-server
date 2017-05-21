'use strict';

angular.module('musicApp').directive('imageFileChooser', [
  function () {
    return {
      restrict: 'E',
      scope: {
        'imageModel': '=',
        'imageSrc': '='
      },
      templateUrl: '/views/imageFileChooser.html',
      link: function(scope, element) {
        scope.fileChooser = function fileChooser() {
          var fileInput = element[0].querySelector('.input-edit-image');
          fileInput.click();
        };
      }
    };
  }
]);
