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
	'angular-storage',
	'ui.router'
]);

musicApp.constant("ServerInfo", {
  "version": '0.4.0'
});

musicApp.config(['$stateProvider', '$urlRouterProvider', function($stateProvider, $urlRouterProvider) {

	$urlRouterProvider.otherwise(function ($injector, $location) {
		var $state = $injector.get("$state");
		$state.go("main.dashboard");
	});

	$stateProvider.state('main', {
		abstract: true,
		templateUrl: 'views/main.html',
		controller: 'MainCtrl',
		data: {
			requiresLogin: true
		}
	})

	.state('authentication', {
		url: '/authentication',
		templateUrl: 'views/authentication.html',
		controller: 'AuthenticationCtrl',
		data: {
			requiresLogin: false
		}
	})

	.state('root', {
		url: '/',
		templateUrl: 'views/dashboard.html',
		controller: 'DashboardCtrl',
		parent: 'main'
	})

	.state('dashboard', {
		url: '/dashboard',
		templateUrl: 'views/dashboard.html',
		controller: 'DashboardCtrl',
		parent: 'main'
	})

	.state('library', {
		url: '/library',
		templateUrl: 'views/library.html',
		controller: 'LibraryCtrl',
		parent: 'main'
	})

	.state('artists', {
		url : '/artists',
		templateUrl: 'views/artists.html',
		controller: 'ArtistsCtrl',
		parent: 'main'
	})

	.state('artist-detail', {
		url: '/artist/:id',
		templateUrl: 'views/artistDetail.html',
		controller: 'ArtistDetailCtrl',
		parent: 'main'
	})

	.state('artist-tracks', {
		url: '/artist/:id/tracks',
		templateUrl: 'views/artistTracks.html',
		controller: 'ArtistTracksCtrl',
		parent: 'main'
	})

	.state('albums', {
		url: '/albums',
		templateUrl: 'views/albums.html',
		controller: 'AlbumsCtrl',
		parent: 'main'
	})

	.state('album-detail', {
		url: '/album/:id',
		templateUrl: 'views/albumDetail.html',
		controller: 'AlbumDetailCtrl',
		parent: 'main'
	})

	.state('tracks', {
		url: '/tracks',
		templateUrl: 'views/tracks.html',
		controller: 'TracksCtrl',
		parent: 'main'
	})

	.state('genres', {
		url: '/genres',
		templateUrl: 'views/genres.html',
		controller: 'GenresCtrl',
		parent: 'main'
	})

	.state('genre-tracks', {
		url: '/genre/:id',
		templateUrl: 'views/genreTracks.html',
		controller: 'GenreTracksCtrl',
		parent: 'main'
	})

	.state('playlists', {
		url: '/playlists',
		templateUrl: 'views/playlists.html',
		controller: 'PlaylistsCtrl',
		parent: 'main'
	})

	.state('playlist-detail', {
		url: '/playlist/:id',
		templateUrl: 'views/playlistDetail.html',
		controller: 'PlaylistDetailCtrl',
		parent: 'main'
	})

	.state('queue', {
		url: '/queue',
		templateUrl: 'views/queue.html',
		controller: 'QueueCtrl',
		parent: 'main'
	});
}]);


musicApp.config(function($httpProvider) {
	$httpProvider.interceptors.push(function($log, $timeout, $q, $injector, User) {
		var $state;

		// this trick must be done so that we don't receive
		// `Uncaught Error: [$injector:cdep] Circular dependency found`
		$timeout(function() {
			$state = $injector.get('$state');
		});

		return {
			request: function(config) {
				if (!config.url.startsWith('api/')) {
					return config;
				}

				var token = User.getToken(); // store.get('auth-token');
				if (token) {
					config.headers.authorization = 'Bearer ' + token;
				}

				return config;
			},
			responseError: function(rejection) {
				if (rejection.config.url === 'api/authentication') {
					return rejection;
				}

				if (rejection.status !== 401) {
					return rejection;
				}

				var deferred = $q.defer();
				$state.go('authentication');
				deferred.reject(rejection);

				return deferred.promise;
			}
		};
	});
});

musicApp.run(['$rootScope', '$state', 'User', function($rootScope, $state, User) {
	$rootScope.$on('$stateChangeStart', function (event, toState, toParams) {
		var requiresLogin = true;
		if (toState.name === 'authentication') {
			return;
		}

		if (typeof toState.data !== 'undefined' && typeof toState.data.requiresLogin !== 'undefined') {
			requiresLogin = toState.data.requiresLogin;
		}

		var token = User.getToken();

		if (requiresLogin && !token) {
			event.preventDefault();
			return $state.go('authentication');
		}
	});
}]);
