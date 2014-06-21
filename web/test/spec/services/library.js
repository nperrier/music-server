'use strict';

describe('Service: Library', function () {

  // load the service's module
  beforeEach(module('musicApp'));

  // instantiate service
  var Library;
  beforeEach(inject(function (_Library_) {
    Library = _Library_;
  }));

  it('should do something', function () {
    expect(!!Library).toBe(true);
  });

});
