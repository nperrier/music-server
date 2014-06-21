'use strict';

describe('Service: ArtistAlbum', function () {

  // load the service's module
  beforeEach(module('musicApp'));

  // instantiate service
  var ArtistAlbum;
  beforeEach(inject(function (_ArtistAlbum_) {
    ArtistAlbum = _ArtistAlbum_;
  }));

  it('should do something', function () {
    expect(!!ArtistAlbum).toBe(true);
  });

});
