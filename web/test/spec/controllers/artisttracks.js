'use strict';

describe('Controller: ArtisttracksCtrl', function () {

  // load the controller's module
  beforeEach(module('musicApp'));

  var ArtisttracksCtrl,
    scope;

  // Initialize the controller and a mock scope
  beforeEach(inject(function ($controller, $rootScope) {
    scope = $rootScope.$new();
    ArtisttracksCtrl = $controller('ArtisttracksCtrl', {
      $scope: scope
      // place here mocked dependencies
    });
  }));

  it('should attach a list of awesomeThings to the scope', function () {
    expect(ArtisttracksCtrl.awesomeThings.length).toBe(3);
  });
});
