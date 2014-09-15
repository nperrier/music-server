// app.js
'use strict';

// depends on ngRoute module
var musicApp = angular.module('musicApp', [
	'ui.bootstrap',
	'ngRoute',
	'ngResource'
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
		templateUrl: 'views/artistdetail.html',
		controller: 'ArtistDetailCtrl'
	})

	.when('/albums', {
		templateUrl: 'views/albums.html',
		controller: 'AlbumsCtrl'
	})

	.when('/album/:albumId', {
		templateUrl: 'views/albumdetail.html',
		controller: 'AlbumDetailCtrl'
	})

	.when('/tracks', {
		templateUrl: 'views/tracks.html',
		controller: 'TracksCtrl'
	})

	.when('/track/:trackId', {
		templateUrl: 'views/trackdetail.html',
		controller: 'TrackDetailCtrl'
	})

	.when('/genres', {
		templateUrl: 'views/genres.html',
		controller: 'GenresCtrl'
	})

	.when('/playlists', {
		templateUrl: 'views/playlists.html',
		controller: 'PlaylistsCtrl'
	})

	.when('/playlist/:playlistId', {
		templateUrl: 'views/playlistdetail.html',
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

