'use strict';

describe('Controller: GenresCtrl', function () {

  // load the controller's module
  beforeEach(module('musicApp'));

  var GenresCtrl,
    scope;

  // Initialize the controller and a mock scope
  beforeEach(inject(function ($controller, $rootScope) {
    scope = $rootScope.$new();
    GenresCtrl = $controller('GenresCtrl', {
      $scope: scope
    });
  }));

  it('should attach a list of awesomeThings to the scope', function () {
    expect(scope.awesomeThings.length).toBe(3);
  });
});
