'use strict';

/**
 * @ngdoc service
 * @name musicApp.PlayerQueue
 * @description
 * # PlayerQueue
 * Service in the musicApp.
 */
angular.module('musicApp')
  .service('PlayerQueue', ['$log', '$rootScope', function($log, $rootScope) {

    var self = this;

    self.queue = []; // a list of tracks, incrementing from 0..total
    self.current = -1; // the index of the currently playing track
    self.total = 0; // the total num of tracks

    self.addTrack = function(track) {
      // add track to end of queue:
      return self.insertTrack(track, self.total);
    };

    self.insertTrack = function(track, position) {

      if (position > self.total || position < 0) {
        $log.error('Attempting to add track to invalid position: ' + position + ', total: ' + self.total);
        return false;
      }

      self.queue.splice(position, 0, track);

      if (self.current >= position) {
        self.current++;
      }
      self.total++;
      $log.info('Added track to queue: ' + track);
      $rootScope.$emit('track.added');

      return true;
    };

    self.removeTrack = function(track, position) {

      var currentRemoved = false;

      if (position >= self.total || position <= 0) {
        $log.error('Attempting to remove track from invalid position: ' + position + ', total: ' + self.total);
        return null;
      }

      if (self.current === position) {
        currentRemoved = true;
      }

      if (self.current <= position) {
        self.current--;
      }

      track = self.queue.splice(position, 1)[0];

      self.total--;
      $log.info('Removed track from queue: ' + track);

      $rootScope.$emit('track.removed');
      if (currentRemoved) {
        // The player must be informed that the current track was removed
        // $rootScope.$emit('track.current.removed');
      }

      return track;
    };

    self.getCurrent = function() {
      if (self.total === 0) {
        return null; // no tracks!
      }

      return self.queue[self.current];
    };

    self.getNext = function() {
      if (self.current === (self.total - 1)) {
        return null; // no more tracks!
      }

      self.current++;
      return self.queue[self.current];
    };

    self.getPrevious = function() {
      if (self.current <= 0) {
        return null; // we're at the beginning!
      }

      self.current--;
      return self.queue[self.current];
    };

    self.getTracks = function() {
      return self.queue;
    };

    self.hasNext = function() {
      return self.current < self.total - 1;
    };

    self.hasPrevious = function() {
      return self.current > 0;
    };
  }]);
