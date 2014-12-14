'use strict';

describe('Controller: AlbumDetailCtrl', function () {

  // load the controller's module
  beforeEach(module('musicApp'));

  var AlbumDetailCtrl,
    scope;

  // Initialize the controller and a mock scope
  beforeEach(inject(function ($controller, $rootScope) {
    scope = $rootScope.$new();
    AlbumDetailCtrl = $controller('AlbumDetailCtrl', {
      $scope: scope
    });
  }));

  it('should attach a list of awesomeThings to the scope', function () {
    expect(scope.awesomeThings.length).toBe(3);
  });
});
