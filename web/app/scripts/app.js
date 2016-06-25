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
		controller: 'AuthenticationCtrl',
    data: {
      requireLogin: false
    }
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


musicApp.config(function($httpProvider) {

  $httpProvider.interceptors.push(function($timeout, $q, $injector) {
    var LoginModal;
    var $http;
    var $state;

    // this trick must be done so that we don't receive
    // `Uncaught Error: [$injector:cdep] Circular dependency found`
    $timeout(function() {
      LoginModal = $injector.get('LoginModal');
      $http = $injector.get('$http');
      $state = $injector.get('$state');
    });

    return {
      responseError: function(rejection) {
        if (rejection.status !== 401 || rejection.url !== 'api/authentication') {
          return rejection;
        }

        var deferred = $q.defer();

        new LoginModal()
          .then(function() {
            deferred.resolve($http(rejection.config));
          })
          .catch(function() {
            $state.go('authentication');
            deferred.reject(rejection);
          });

        return deferred.promise;
      }
    };
  });

});

// musicApp.run(function( /* injectables */ ) { // instance-injector
  // This is an example of a run block. You can have as many of these as you want.
  // You can only inject instances (not Providers) into run blocks
// });
