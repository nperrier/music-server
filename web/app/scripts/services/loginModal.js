'use strict';

/**
 * @ngdoc service
 * @name musicApp.LoginModal
 * @description
 * # LoginModal
 * Service in the musicApp.
 */
angular.module('musicApp').service('LoginModal', [
  '$modal', '$rootScope', function ($modal, $rootScope) {

    function assignCurrentUser(username) {
      $rootScope.currentUser = username;
      return username;
    }

    return function() {
      var instance = $modal.open({
        templateUrl: 'views/loginModal.html',
        controller: 'LoginModalCtrl',
        controllerAs: 'LoginModalCtrl'
        // size: 'sm'
      });

      return instance.result.then(assignCurrentUser);
    };
  }
]);

angular.module('musicApp').run(['$rootScope', '$state', 'store',
  function($rootScope, $state, store) {

  $rootScope.$on('$stateChangeStart', function (event, toState, toParams) {
    var requireLogin = true;
    if (toState.name === 'authentication') {
      return;
    }

    if (typeof toState.data !== 'undefined' && typeof toState.data.requireLogin !== 'undefined') {
      requireLogin = toState.data.requireLogin;
    }

    var token = store.get('auth-token');

    if (requireLogin && token === null) {
      event.preventDefault();

      // new LoginModal()
      //   .then(function() {
      //     return $state.go(toState.name, toParams);
      //   })
      //   .catch(function(reason) {
      //     console.debug(reason);
      //     // We get here if the modal was dismissed

      return $state.go('authentication');
        // });
    }
  });
}]);
