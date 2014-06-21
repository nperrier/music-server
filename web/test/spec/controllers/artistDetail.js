'use strict';

describe('Controller: ArtistDetailCtrl', function () {

  // load the controller's module
  beforeEach(module('musicApp'));

  var ArtistDetailCtrl,
    scope;

  // Initialize the controller and a mock scope
  beforeEach(inject(function ($controller, $rootScope) {
    scope = $rootScope.$new();
    ArtistDetailCtrl = $controller('ArtistDetailCtrl', {
      $scope: scope
    });
  }));

  it('should attach a list of awesomeThings to the scope', function () {
    expect(scope.awesomeThings.length).toBe(3);
  });
});
