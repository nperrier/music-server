'use strict';

describe('Controller: TrackDetailCtrl', function () {

  // load the controller's module
  beforeEach(module('musicApp'));

  var TrackDetailCtrl,
    scope;

  // Initialize the controller and a mock scope
  beforeEach(inject(function ($controller, $rootScope) {
    scope = $rootScope.$new();
    TrackDetailCtrl = $controller('TrackDetailCtrl', {
      $scope: scope
    });
  }));

  it('should attach a list of awesomeThings to the scope', function () {
    expect(scope.awesomeThings.length).toBe(3);
  });
});
