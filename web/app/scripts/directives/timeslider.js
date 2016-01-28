'use strict';

angular.module('musicApp')
  .directive('timeslider', ['$log', '$rootScope', '$timeout', '$document',
    function($log, $rootScope, $timeout, $document) {
      return {
        restrict: 'E',
        scope: {
          floor: '@',
          ceiling: '@',
          step: '@',
          highlight: '@',
          precision: '@',
          model: '=?ngModel'
        },
        template: '<div class="bar">\n' +
                  '  <div class="selection"></div>\n' +
                  '</div>\n' +
                  '<div class="handle model"></div>\n' +
                  '<div class="bubble limit model">{{ floor }}</div>\n' +
                  '<div class="bubble value model">{{ model || 0 }}</div>\n',
        // controller: function($scope, $element) {}
        // link: function($scope, element, attrs) {}
        compile: function(telement, tattributes) {

          // Helper Functions

          var roundStep = function(value, precision, step, floor) {
            var decimals;
            var remainder;
            var roundedValue;
            var steppedValue;

            if (floor == null) {
              floor = 0;
            }
            if (step == null) {
              step = 1 / Math.pow(10, precision);
            }

            remainder = (value - floor) % step;
            steppedValue = remainder > (step / 2) ? value + step - remainder : value - remainder;
            decimals = Math.pow(10, precision);
            roundedValue = steppedValue * decimals / decimals;

            return parseFloat(roundedValue.toFixed(precision));
          };

          var width = function(element) {
            return element[0].offsetWidth;
          };

          var halfWidth = function(element) {
            return width(element) / 2;
          };

          var offsetLeft = function(element) {
            return element[0].offsetLeft;
          };

          var pixelize = function(position) {
            return position + 'px';
          };

          var offset = function(element, position) {
            return element.css({
              left: position
            });
          };

          var inputEvents = {
            mouse: {
              start: 'mousedown',
              move:  'mousemove',
              end:   'mouseup'
            },
            touch: {
              start: 'touchstart',
              move:  'touchmove',
              end:   'touchend'
            }
          };

          return {
            post: function(scope, element, attributes) {

              var watchables      = ['floor', 'model'];
              var boundToInputs   = false;
              var handleHalfWidth = undefined;
              var barWidth        = undefined;
              var minOffset       = undefined;
              var maxOffset       = undefined;
              var minValue        = undefined;
              var maxValue        = undefined;
              var valueRange      = undefined;
              var offsetRange     = undefined;

              // children elements
              var barElement    = angular.element(element.children()[0]);
              var handleElement = angular.element(element.children()[1]);
              var bubbleElement = angular.element(element.children()[3]);

              var selection = angular.element(barElement.children()[0]);

              if (!attributes.highlight) {
                selection.remove();
              }


              var dimensions = function() {
                if (scope.step == null) {
                  scope.step = 1;
                }
                if (scope.floor == null) {
                  scope.floor = 0;
                }
                if (scope.precision == null) {
                  scope.precision = 0;
                }

                handleHalfWidth = halfWidth(handleElement);
                barWidth        = width(barElement);
                minOffset       = 0;
                maxOffset       = barWidth - width(handleElement);
                minValue        = parseFloat(scope.floor);
                maxValue        = parseFloat(scope.ceiling);
                valueRange      = maxValue - minValue;
                offsetRange     = maxOffset - minOffset;
              };


              var updateDOM = function() {
                dimensions();

                var percentOffset = function(offset) {
                  return ((offset - minOffset) / offsetRange) * 100;
                };

                var percentValue = function(value) {
                  return ((value - minValue) / valueRange) * 100;
                };

                var percentToOffset = function(percent) {
                  return pixelize(percent * offsetRange / 100);
                };

                var setPointers = function() {
                  var newValue = percentValue(scope.model);

                  offset(handleElement, percentToOffset(newValue));
                  offset(bubbleElement, pixelize(offsetLeft(handleElement) - (halfWidth(bubbleElement)) + handleHalfWidth));
                  offset(selection, pixelize(offsetLeft(handleElement) + handleHalfWidth));

                  switch (true) {
                    case attributes.highlight === 'right':
                      return selection.css({
                        width: percentToOffset(110 - newValue)
                      });
                    case attributes.highlight === 'left':
                      selection.css({
                        width: percentToOffset(newValue)
                      });
                      return offset(selection, 0);
                  }
                };


                var bindToInputEvents = function(handleElement, bubbleElement, events) {

                  var onEnd = function() {
                    bubbleElement.removeClass('active');
                    handleElement.removeClass('active');
                    $document.unbind(events.move);
                    $document.unbind(events.end);
                    // TODO: event: track position changed: emit value
                    scope.$emit('slider.dropped', scope.model);
                    // TODO: event: restore automatic updates to pointer
                    return scope.$apply();
                  };

                  var onMove = function(event) {
                    var eventX     = event.clientX || event.touches[0].clientX;
                    var newOffset  = eventX - element[0].getBoundingClientRect().left - handleHalfWidth;
                    newOffset      = Math.max(Math.min(newOffset, maxOffset), minOffset);
                    var newPercent = percentOffset(newOffset);
                    var newValue   = minValue + (valueRange * newPercent / 100.0);
                    var step       = parseFloat(scope.step);
                    var precision  = parseInt(scope.precision);
                    var floor      = parseFloat(scope.floor);
                    newValue       = roundStep(newValue, precision, step, floor);

                    scope.model = newValue;

                    setPointers();

                    return scope.$apply();
                  };

                  var onStart = function(event) {
                    dimensions();
                    bubbleElement.addClass('active');
                    handleElement.addClass('active');
                    setPointers();
                    // TODO: event: prevent automatic updates to pointer
                    scope.$emit('slider.dragging');
                    event.stopPropagation();
                    event.preventDefault();
                    $document.bind(events.move, onMove);

                    return $document.bind(events.end, onEnd);
                  };

                  return handleElement.bind(events.start, onStart);
                };


                var setBindings = function() {
                  boundToInputs = true;
                  var inputTypes = ['touch', 'mouse'];
                  var boundVariables = [];
                  for (var j = 0; j < inputTypes.length; j++) {
                    var type = inputTypes[j];
                    var bound = bindToInputEvents(handleElement, bubbleElement, inputEvents[type]);
                    boundVariables.push(bound);
                  }

                  return boundVariables;
                };

                if (!boundToInputs) {
                  setBindings();
                }

                return setPointers();
              };

              $timeout(updateDOM);

              for (var j = 0; j < watchables.length; j++) {
                var w = watchables[j];
                scope.$watch(w, updateDOM, true);
              }

              return window.addEventListener("resize", updateDOM);
            }
          };
        },
        controller: function($scope, $element) {

        }
      };
    }
  ]);
