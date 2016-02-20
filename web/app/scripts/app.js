// app.js
'use strict';

// hack to inject underscore lib
angular.module('underscore', [])
  .factory('_', ['$window', function($window) {
    return $window._; // assumes underscore has already been loaded on the page
  }]);

// hack to inject moment lib
angular.module('moment', [])
  .factory('moment', ['$window', function($window) {
    return $window.moment; // assumes moment has already been loaded on the page
  }]);

// depends on ngRoute module
var musicApp = angular.module('musicApp', [
	'ui.bootstrap',
	'ngRoute',
	'ngResource',
	'ngAnimate',
	'angularSpinner',
	'vs-repeat',
	'underscore',
	'moment'
]);

musicApp.config(['$routeProvider', function($routeProvider) {

	$routeProvider.when('/', {
		templateUrl : 'views/dashboard.html',
		controller  : 'DashboardCtrl'
	})

	.when('/dashboard', {
		templateUrl : 'views/dashboard.html',
		controller  : 'DashboardCtrl'
	})

	.when('/library', {
		templateUrl : 'views/library.html',
		controller  : 'LibraryCtrl'
	})

	.when('/artists', {
		templateUrl: 'views/artists.html',
		controller: 'ArtistsCtrl'
	})

	.when('/artist/:artistId', {
		templateUrl: 'views/artistDetail.html',
		controller: 'ArtistDetailCtrl'
	})

	.when('/artist/:artistId/tracks', {
		templateUrl: 'views/artistTracks.html',
		controller: 'ArtistTracksCtrl'
	})

	.when('/albums', {
		templateUrl: 'views/albums.html',
		controller: 'AlbumsCtrl'
	})

	.when('/album/:albumId', {
		templateUrl: 'views/albumDetail.html',
		controller: 'AlbumDetailCtrl'
	})

	.when('/tracks', {
		templateUrl: 'views/tracks.html',
		controller: 'TracksCtrl'
	})

	.when('/genres', {
		templateUrl: 'views/genres.html',
		controller: 'GenresCtrl'
	})

	.when('/genre/:genreId', {
		templateUrl: 'views/genreTracks.html',
		controller: 'GenreTracksCtrl'
	})

	.when('/playlists', {
		templateUrl: 'views/playlists.html',
		controller: 'PlaylistsCtrl'
	})

	.when('/playlist/:playlistId', {
		templateUrl: 'views/playlistDetail.html',
		controller: 'PlaylistDetailCtrl'
	})

  .when('/queue', {
		templateUrl: 'views/queue.html',
		controller: 'QueueCtrl'
	})

	.otherwise({
		redirectTo: '/dashboard'
	});
}]);
