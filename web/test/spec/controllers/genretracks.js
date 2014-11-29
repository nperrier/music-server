'use strict';

describe('Controller: GenretracksCtrl', function () {

  // load the controller's module
  beforeEach(module('musicApp'));

  var GenretracksCtrl,
    scope;

  // Initialize the controller and a mock scope
  beforeEach(inject(function ($controller, $rootScope) {
    scope = $rootScope.$new();
    GenretracksCtrl = $controller('GenretracksCtrl', {
      $scope: scope
    });
  }));

  it('should attach a list of awesomeThings to the scope', function () {
    expect(scope.awesomeThings.length).toBe(3);
  });
});
