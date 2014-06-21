'use strict';

/**
 * @ngdoc service
 * @name musicApp.Artist
 * @description
 * # Artist
 * Factory in the musicApp.
 */
angular.module('musicApp')
  .factory('Artist', ['$resource', function($resource) {
	return $resource('api/artist/:artistId', {}, {
		query: {
			method: 'GET',
			isArray: true
		},
		get: {
			method: 'GET',
			params: { artistId: '@artistId' }
		}
	});
}]);
