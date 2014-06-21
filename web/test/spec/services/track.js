'use strict';

describe('Service: Track', function () {

  // load the service's module
  beforeEach(module('musicApp'));

  // instantiate service
  var Track;
  beforeEach(inject(function (_Track_) {
    Track = _Track_;
  }));

  it('should do something', function () {
    expect(!!Track).toBe(true);
  });

});
