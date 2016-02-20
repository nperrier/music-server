'use strict';

/**
 * @ngdoc service
 * @name musicApp.Artist
 * @description
 * # Artist
 * Factory in the musicApp.
 */
angular.module('musicApp').factory('Artist', [
	'$resource', function($resource) {

		return $resource('api/artist/:artistId', {}, {
			query: {
				method: 'GET',
				isArray: true
			},
			get: {
				method: 'GET',
				params: { artistId: '@artistId' }
			},
			getTracks: {
	      url: 'api/artist/:artistId/tracks',
	      method: 'GET',
	      isArray: true,
	      params: { artistId: '@artistId' }
	    },
      getAlbums: {
      	url: 'api/artist/:artistId/albums',
        method: 'GET',
        params: { artistId: '@artistId' },
        isArray: true
      }
		});
  }
]);
