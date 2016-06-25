'use strict';

describe('Controller: LoginModalCtrl', function () {

  // load the controller's module
  beforeEach(module('musicApp'));

  var loginModalCtrl;
  var scope;

  // Initialize the controller and a mock scope
  beforeEach(inject(function ($controller, $rootScope) {
    scope = $rootScope.$new();
    loginModalCtrl = $controller('LoginModalCtrl', {
      $scope: scope
      // place here mocked dependencies
    });
  }));

  it('should attach a list of awesomeThings to the scope', function () {
    expect(LoginModalCtrl.awesomeThings.length).toBe(3);
  });
});
