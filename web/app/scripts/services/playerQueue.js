'use strict';

/**
 * @ngdoc service
 * @name musicApp.PlayerQueue
 * @description
 * # PlayerQueue
 * Service in the musicApp.
 */
angular.module('musicApp').service('PlayerQueue', [
  '$log', '$rootScope', function ($log, $rootScope) {

    var self = this;

    self.queue = []; // a list of tracks, incrementing from 0..total
    self.current = -1; // the index of the track currently loaded into the AudioPlayer

    self.addTrack = function(track) {
      // add track to end of queue:
      return self.insertTrack(track, self.queue.length);
    };

    self.addTracks = function(tracks) {
      tracks.forEach(function(track) {
        var success = self.insertTrack(track, self.queue.length);
        if (!success) {
          return false;
        }
      });
      return true;
    };

    self.insertTrack = function(track, position) {
      if (position > self.queue.length || position < 0) {
        $log.error('Attempting to add track to invalid position: ' + position + ', total: ' + self.queue.length);
        return false;
      }

      self.queue.splice(position, 0, track);

      if (self.current >= position) {
        self.current++;
      }

      $log.debug('Added track to queue: ' + track);
      $rootScope.$emit('queue.track.added', track);

      return true;
    };

    self.removeTrack = function(position) {
      var currentRemoved = false;

      if (position > self.queue.length || position < 0) {
        $log.error('Attempting to remove track from invalid position: ' + position + ', total: ' + self.queue.length);
        return null;
      }

      if (self.current === position) {
        currentRemoved = true;
      }

      if (self.current >= position) {
        self.current--;
      }

      var track = self.queue.splice(position, 1)[0];

      $log.debug('Removed track from queue, track: ' + track);
      $rootScope.$emit('queue.tracks.removed', [ track ]);

      return track;
    };

    self.clear = function () {
      if (self.queue.length !== 0) {
        var removedTracks = self.queue.slice(0); // copy the array
        self.queue.splice(0); // clear the array
        // self.queue.length = 0;
        self.current = -1;
        // Let interested parties know that the current track was removed
        $rootScope.$emit('queue.tracks.removed', removedTracks);
      }
    };

    self.getCurrentIndex = function () {
      if (self.queue.length === 0) {
        return null; // no tracks!
      }

      return self.current;
    };

    self.getNext = function() {
      if (self.current === (self.queue.length - 1)) {
         // no more tracks, reset to the beginning of the queue
         self.current = -1;
        return null;
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
      return self.current < (self.queue.length - 1);
    };

    self.hasPrevious = function() {
      return self.current > 0;
    };
  }
]);
