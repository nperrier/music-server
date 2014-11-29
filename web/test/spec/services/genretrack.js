'use strict';

describe('Service: genretrack', function () {

  // load the service's module
  beforeEach(module('musicApp'));

  // instantiate service
  var genretrack;
  beforeEach(inject(function (_genretrack_) {
    genretrack = _genretrack_;
  }));

  it('should do something', function () {
    expect(!!genretrack).toBe(true);
  });

});
