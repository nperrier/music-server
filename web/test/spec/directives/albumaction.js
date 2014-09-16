'use strict';

describe('Directive: albumAction', function () {

  // load the directive's module
  beforeEach(module('musicApp'));

  var element,
    scope;

  beforeEach(inject(function ($rootScope) {
    scope = $rootScope.$new();
  }));

  it('should make hidden element visible', inject(function ($compile) {
    element = angular.element('<album-action></album-action>');
    element = $compile(element)(scope);
    expect(element.text()).toBe('this is the albumAction directive');
  }));
});
