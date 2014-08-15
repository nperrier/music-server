'use strict';
/* global moment */

/**
 * @ngdoc filter
 * @name musicApp.filter:duration
 * @function
 * @description
 * # duration: input: 270000, output: '04:30'
 * Filter in the musicAppFilters.
 */
angular.module('musicAppFilters', [])
  .filter('duration', function () {

    return function (durationInMillis) {

      function zeroPad(value) {
        return (value < 10) ? '0' + value : value;
      }

      var d = moment.duration(durationInMillis);
      var duration = '';
      // if > 1 hour, then display HH:MM:SS
      if (d.hours() > 0) {
        var hours = zeroPad(d.hours());
        duration = hours + ':';
      }
      var minutes = zeroPad(d.minutes());
      var seconds = zeroPad(d.seconds());
      // display MM:SS
      duration += minutes + ':' + seconds;

      return duration;
    };
  });
