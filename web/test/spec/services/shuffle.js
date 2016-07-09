'use strict';

describe('Service: shuffle', function () {

  // load the service's module
  beforeEach(module('musicApp'));

  // instantiate service
  var shuffle;
  beforeEach(inject(function (_shuffle_) {
    shuffle = _shuffle_;
  }));

  it('should do something', function () {
    expect(!!shuffle).toBe(true);
  });

});
