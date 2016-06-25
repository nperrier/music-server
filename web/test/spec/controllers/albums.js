'use strict';

ddescribe('Controller: AlbumsCtrl', function () {

  // load the controller's module
  beforeEach(module('musicApp'));

  // mock Album factory:
  beforeEach(module(function($provide) {
    $provide.factory('Album', function() {
      var query = jasmine.createSpy('query').and.callFake(function() {
        return [];
      });
      var getTracks = jasmine.createSpy('getTracks').and.callFake(function() {
        return [];
      });
      return {
        query: query,
        getTracks: getTracks
      };
    });
  }));

  beforeEach(module(function($provide) {
    $provide.factory('Playlist', function() {
      var query = jasmine.createSpy('query').and.callFake(function() {
        return [];
      });
      var addAlbum = jasmine.createSpy('addAlbum').and.callFake(function(albumId, playlistId) {

      });
      return {
        query: query,
        addAlbum: addAlbum
      };
    });
  }));

  var AlbumsCtrl;
  var scope;
  var $rootScope;
  var $controller;
  var $log;
  var $timeout;
  var _;
  var usSpinnerService;
  var Album;
  var Playlist;
  var PlayerQueue;

  // Initialize the controller and a mock scope
  beforeEach(inject(function($injector) {
    // Set up the mock http service responses
    $rootScope       = $injector.get('$rootScope');
    $controller      = $injector.get('$controller');
    $log             = $injector.get('$log');
    $timeout         = $injector.get('$timeout');
    _                = $injector.get('_');
    usSpinnerService = $injector.get('usSpinnerService');
    Album            = $injector.get('Album');
    Playlist         = $injector.get('Playlist');
    PlayerQueue      = $injector.get('PlayerQueue');

    scope = $rootScope.$new();
    AlbumsCtrl = $controller('AlbumsCtrl', {
      '$scope': scope,
      '$log': $log,
      '$timeout': $timeout,
      '_': _,
      'usSpinnerService': usSpinnerService,
      'Album': Album,
      'Playlist': Playlist,
      'PlayerQueue': PlayerQueue
    });

  }));



  it('should get all albums on load', function () {
    expect(scope.albums).toEqual([]);
  });

  it('should get all playlists on load', function () {
    expect(scope.playlists).toEqual([]);
  });

  it('should add an album to a playlist', function () {
    // debugger;
    var album = { id: 1 };
    var playlist = { id: 1 };
    scope.addAlbumToPlaylist(album, playlist);
    expect(Playlist.addAlbum).toHaveBeenCalled();
  });
});
