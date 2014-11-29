'use strict';

describe('Directive: albumActionMenu', function () {

  // load the directive's module
  beforeEach(module('musicApp'));

  var element;
  var scope;

  beforeEach(inject(function ($rootScope) {
    scope = $rootScope.$new();
  }));

  it('should make hidden element visible', inject(function ($compile) {
    element = angular.element('<album-action-menu></album-action-menu>');
    element = $compile(element)(scope);
    expect(element.text()).toBe('this is the albumActionMenu directive');
  }));
});
