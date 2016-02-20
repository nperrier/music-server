'use strict';

/**
 * @ngdoc service
 * @name musicApp.AudioPlayer
 * @description
 * # AudioPlayer
 * Service in the musicApp.
 */
angular.module('musicApp').service('AudioPlayer', [
  '$log', '$rootScope', function ($log, $rootScope) {

    var self = this;

    self.audio = new Audio();
    self.track = null;

    self.getTrack = function() {
      return self.track;
    };

    self.setVolume = function(vol) {
      self.audio.volume = vol;
    };

    self.getVolume = function() {
      return self.audio.volume;
    };

    self.getTrackLength = function() {
      if (self.track) {
        return self.track.length;
      }
      return 0;
    };

    self.getTrackTime = function() {
      if (self.track) {
        return self.audio.currentTime;
      }
      return 0;
    };

    // self.getTrack = function() {
    //   return self.Track;
    // }

    self.getDuration = function() {
      if (self.track) {
        return self.track.length;
      }
      return 0;
    };

    // seek to a new audio position
    self.seek = function(seekPercentage) {
      if (!self.isTrackLoaded()) {
        return;
      }

      if (seekPercentage < 0 || seekPercentage > 100) {
        throw new Error('Invalid seek percentage');
      }

      var duration = self.getDuration() / 1000; // to seconds
      var seekTime = Math.floor(((seekPercentage / 100) * duration));
      // TODO: may need handle edge cases (0 or 100)
      $log.debug('Seeking to position: ' + seekTime);
      self.audio.currentTime = seekTime;
    };

    // Tell audio element to play
    self.play = function() {
      if (!self.isTrackLoaded()) {
        $log.debug('Track is not loaded!');
        return false;
      }

      if (!self.audio.paused) {
        $log.warn('Track is already playing');
        return false;
      }

      self.audio.play();
      return true;
    };

    // Tell audio element to pause
    self.pause = function() {
      if (!self.isTrackLoaded()) {
        $log.warn('Track is not loaded');
        return false;
      }

      if (self.audio.paused) {
        $log.warn('Track is already paused');
        return false;
      }

      self.audio.pause();
      return true;
    };

    // play the given track immediately
    self.playNow = function(track) {
      self.resetPlayer();
      self.track = track;
      self.audio.src = track.streamUrl;
      self.audio.load();
      self.audio.play();
    };

    self.resetPlayer = function() {
      self.audio.pause();
      self.audio.removeAttribute('src');
      self.track = null;
    };

    self.isPlaying = function() {
      if (self.isTrackLoaded()) {
        return !self.audio.paused;
      }
      return false;
    };

    self.isPaused = function() {
      return !self.isPlaying(); // || !self.audio.ended;
    };

    self.isTrackLoaded = function(track) {
      if (track && self.track) {
        return self.track.id === track.id;
      }
      return !!self.track; //!!self.audio.src;
    };

    // listen for audio-element events, and broadcast stuff
    self.audio.addEventListener('play', function() {
      $rootScope.$emit('audio.play', self);
    });

    self.audio.addEventListener('pause', function() {
      $rootScope.$emit('audio.pause', self);
    });

    self.audio.addEventListener('ended', function() {
      $rootScope.$emit('audio.ended', self);
    });

    // self.audio.addEventListener('timeupdate', function() {
    //   $rootScope.$emit('audio.time', self);
    // });

  }
]);
