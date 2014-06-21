'use strict';

describe('Service: Artist', function () {

  // load the service's module
  beforeEach(module('musicApp'));

  // instantiate service
  var Artist;
  beforeEach(inject(function (_Artist_) {
    Artist = _Artist_;
  }));

  it('should do something', function () {
    expect(!!Artist).toBe(true);
  });

});
