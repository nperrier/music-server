'use strict';

describe('Controller: LeftNavCtrl', function () {

  // load the controller's module
  beforeEach(module('musicApp'));

  var LeftNavCtrl,
    scope;

  // Initialize the controller and a mock scope
  beforeEach(inject(function ($controller, $rootScope) {
    scope = $rootScope.$new();
    LeftNavCtrl = $controller('LeftNavCtrl', {
      $scope: scope
    });
  }));

  it('should attach a list of awesomeThings to the scope', function () {
    expect(scope.awesomeThings.length).toBe(3);
  });
});
