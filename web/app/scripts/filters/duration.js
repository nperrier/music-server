'use strict';

/**
 * @ngdoc filter
 * @name musicApp.filter:duration
 * @function
 * @description
 * # duration: input: 270000, output: '04:30'
 * Filter in the musicApp.
 */
angular.module('musicApp')
  .filter('duration', ['_', 'moment', function (_, moment) {

    return function (duration, unit) {
      // default to milliseconds
      if (_.isUndefined(unit)) {
        unit = 'milliseconds';
      }

      function zeroPad(value) {
        return (value < 10) ? '0' + value : value;
      }

      var d = moment.duration(duration, unit);
      var formattedDuration = '';
      // if > 1 hour, then display HH:MM:SS
      if (d.hours() > 0) {
        var hours = zeroPad(d.hours());
        formattedDuration = hours + ':';
      }
      var minutes = zeroPad(d.minutes());
      var seconds = zeroPad(d.seconds());
      // display MM:SS
      formattedDuration += minutes + ':' + seconds;

      return formattedDuration;
    };
  }]);
