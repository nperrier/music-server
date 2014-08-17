'use strict';

describe('Controller: PlaylistDetailCtrl', function () {

  // load the controller's module
  beforeEach(module('musicApp'));

  var PlaylistDetailCtrl, scope;

  // Initialize the controller and a mock scope
  beforeEach(inject(function ($controller, $rootScope) {
    scope = $rootScope.$new();
    PlaylistDetailCtrl = $controller('PlaylistDetailCtrl', {
      $scope: scope
    });
  }));

  it('should attach a list of awesomeThings to the scope', function () {
    expect(scope.awesomeThings.length).toBe(3);
  });
});
