'use strict';

/**
 * @ngdoc factory
 * @name musicApp.LoadingSpinner
 * @description
 * # LoadingSpinner
 * Factory in the musicApp.
 */
angular.module('musicApp').factory('LoadingSpinner', [
  'usSpinnerService',
  '$timeout',
  function(
    usSpinnerService,
    $timeout
  ) {

    return function(scope, numberPendingRequests) {

      var self = this;

      self.scope = scope;
      self.numberPendingRequests = numberPendingRequests || 1;

      self.start = function() {
        // wait 1.5 seconds before showing spinner
        $timeout(function() {
          if (!self.scope.doneLoading) {
            usSpinnerService.spin('spinner-loading');
          }
        }, 1500);
      };

      self.stop = function() {
        usSpinnerService.stop('spinner-loading');
        self.scope.doneLoading = true;
      };

      self.checkDoneLoading = function() {
        self.numberPendingRequests--;
        if (self.numberPendingRequests <= 0) {
          self.stop();
        }
      };
    };
  }
]);
