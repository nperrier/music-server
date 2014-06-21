'use strict';

describe('Controller: TracksCtrl', function () {

  // load the controller's module
  beforeEach(module('musicApp'));

  var TracksCtrl,
    scope;

  // Initialize the controller and a mock scope
  beforeEach(inject(function ($controller, $rootScope) {
    scope = $rootScope.$new();
    TracksCtrl = $controller('TracksCtrl', {
      $scope: scope
    });
  }));

  it('should attach a list of awesomeThings to the scope', function () {
    expect(scope.awesomeThings.length).toBe(3);
  });
});
