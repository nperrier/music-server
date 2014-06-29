'use strict';

describe('Service: Genre', function () {

  // load the service's module
  beforeEach(module('musicApp'));

  // instantiate service
  var Genre;
  beforeEach(inject(function (_Genre_) {
    Genre = _Genre_;
  }));

  it('should do something', function () {
    expect(!!Genre).toBe(true);
  });

});
