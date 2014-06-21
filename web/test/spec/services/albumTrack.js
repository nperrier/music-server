'use strict';

describe('Service: AlbumTrack', function () {

  // load the service's module
  beforeEach(module('musicApp'));

  // instantiate service
  var AlbumTrack;
  beforeEach(inject(function (_AlbumTrack_) {
    AlbumTrack = _AlbumTrack_;
  }));

  it('should do something', function () {
    expect(!!AlbumTrack).toBe(true);
  });

});
