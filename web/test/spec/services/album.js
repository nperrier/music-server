'use strict';

describe('Service: Album', function () {

  // instantiate service
  var Album;
  var $httpBackend;
  var response;

  // load the service's module
  beforeEach(module('musicApp'));

  beforeEach(inject(function($injector) {
    // Set up the mock http service responses
    $httpBackend = $injector.get('$httpBackend');
    Album = $injector.get('Album');
  }));


  it('should get all albums', function () {
    $httpBackend.when('GET', 'api/album').respond([]);
    response = Album.query();
    $httpBackend.flush();
    expect(response).toEqual([]);
  });

  // it('should get an album by id', function () {
  //   $httpBackend.when('GET', 'api/album/' + album.id).respond({});
  //   Album.get({albumId: album.id});
  // });

  // it('should get tracks for an album', function () {
  //   $httpBackend.when('GET', 'api/album/' + album.id + '/tracks').respond([]);
  //   Album.getTracks({albumId: album.id});
  // });
});
