'use strict';
// TODO
angular.module('musicApp').service('CoverArtService', [
  '$http',
  '$q',
  function(
    $http,
    $q
  ) {

    return {
      upload: function upload(file, trackId) {
        var upl = $http({
          method: 'PUT',
          url: 'api/cover/track/' + trackId,
          // Covers a bug in the browser, let it add the 'boundry=' param:
          headers: {
            'Content-Type': undefined
          },
          data: {
            file: file
          },
          transformRequest: function(data, headersGetter) {
            var formData = new FormData();
            angular.forEach(data, function(value, key) {
              formData.append(key, value);
            });

            var headers = headersGetter();
            delete headers['content-type'];

            return formData;
          }
        });

        return upl.then(
          function(response) {
            return response;
          },
          function(response) {
            if (!angular.isObject(response.data) || !response.data.message) {
              return $q.reject('An unknown error occurred');
            }
            return $q.reject(response.data.message);
          }
        );
      }
    };
  }
]);
