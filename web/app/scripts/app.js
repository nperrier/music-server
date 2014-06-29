// app.js
'use strict';

// depends on ngRoute module
var musicApp = angular.module('musicApp', [
	'ui.bootstrap',
	'ngRoute',
	'ngResource',
	'musicAppFilters',
	'audioPlayer-directive'
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

	.when('/track/:trackId', {
		templateUrl: 'views/trackDetail.html',
		controller: 'TrackDetailCtrl'
	})

	.when('/genres', {
		templateUrl: 'views/genres.html',
		controller: 'GenresCtrl'
	})

	.otherwise({
		redirectTo: '/dashboard'
	});
}]);

