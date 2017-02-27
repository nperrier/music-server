'use strict';

angular.module('musicApp').directive('imageFileChooser', [
  function () {

    return {
      restrict: 'E',
      scope: {
        'imageModel': '=',
        'imageSrc': '='
      },
      template: '' +
          '<img ng-src="{{imageSrc}}" class="track-edit-image" ng-click="fileChooser()"/>' +
          '<input class="input-edit-image" type="file" style="display: none;" ' +
                 'file-input="imageModel.file" file-preview="imageSrc" />',
      link: function(scope, element) {
        scope.fileChooser = function fileChooser() {
          var fileInput = element[0].querySelector('.input-edit-image');
          fileInput.click();
        };
      }
    };
  }
]);
