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
	'moment',
	'angular-sortable-view',
	'ui.router'
]);

musicApp.config(['$stateProvider', '$urlRouterProvider', function($stateProvider, $urlRouterProvider) {

	$urlRouterProvider.otherwise('/dashboard');

	$stateProvider.state('root', {
		url: '/',
		templateUrl: 'views/dashboard.html',
		controller: 'DashboardCtrl'
	})

	.state('dashboard', {
		url: '/dashboard',
		templateUrl: 'views/dashboard.html',
		controller: 'DashboardCtrl'
	})

  .state('authentication', {
		url: '/authentication',
		templateUrl: 'views/authentication.html',
		controller: 'AuthenticationCtrl'
	})

	.state('library', {
		url: '/library',
		templateUrl: 'views/library.html',
		controller: 'LibraryCtrl'
	})

	.state('artists', {
		url : '/artists',
		templateUrl: 'views/artists.html',
		controller: 'ArtistsCtrl'
	})

	.state('artist-detail', {
		url: '/artist/:id',
		templateUrl: 'views/artistDetail.html',
		controller: 'ArtistDetailCtrl'
	})

	.state('artist-tracks', {
		url: '/artist/:id/tracks',
		templateUrl: 'views/artistTracks.html',
		controller: 'ArtistTracksCtrl'
	})

	.state('albums', {
		url: '/albums',
		templateUrl: 'views/albums.html',
		controller: 'AlbumsCtrl'
	})

	.state('album-detail', {
		url: '/album/:id',
		templateUrl: 'views/albumDetail.html',
		controller: 'AlbumDetailCtrl'
	})

	.state('tracks', {
		url: '/tracks',
		templateUrl: 'views/tracks.html',
		controller: 'TracksCtrl'
	})

	.state('genres', {
		url: '/genres',
		templateUrl: 'views/genres.html',
		controller: 'GenresCtrl'
	})

	.state('genre-tracks', {
		url: '/genre/:id',
		templateUrl: 'views/genreTracks.html',
		controller: 'GenreTracksCtrl'
	})

	.state('playlists', {
		url: '/playlists',
		templateUrl: 'views/playlists.html',
		controller: 'PlaylistsCtrl'
	})

	.state('playlist-detail', {
		url: '/playlist/:id',
		templateUrl: 'views/playlistDetail.html',
		controller: 'PlaylistDetailCtrl'
	})

  .state('queue', {
  	url: '/queue',
		templateUrl: 'views/queue.html',
		controller: 'QueueCtrl'
	});

}]);
