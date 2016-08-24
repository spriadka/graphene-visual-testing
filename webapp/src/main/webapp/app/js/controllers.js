/* global angular, Promise, Integer, _ */

'use strict';

/* Controllers */

var visualTestingControllers = angular.module('visualTestingControllers', []);

visualTestingControllers.controller('SuiteListCtrl', ['$scope', '$route', '$log',
    'Suites', 'ParticularSuite', 'DeleteParticularSuite',
    function ($scope, $route, $log, Suites, ParticularSuite, DeleteParticularSuite) {
        $scope.lastRun = function (suite) {
            return suite.runs[suite.runs.length - 1];
        };
        $scope.suites = Suites.query();
        $scope.count = 0;
        $scope.deleteSuite = function (testSuiteID) {
            var promise =
                    DeleteParticularSuite.deleteParticularSuite(testSuiteID);
            promise.then(
                    function (payload) {
                        $route.reload();
                        $log.info('suite deleted sucessfully', payload);
                    },
                    function (errorPayload) {
                        $log.error('failure delete suite', errorPayload);
                    });
        };
        $scope.query = "";
    }]);

visualTestingControllers.controller('ParticularSuiteCtrl', ['$scope', '$routeParams',
    '$route', '$log', 'DeleteParticularSuiteRun', 'ParticularRun', 'AcceptSampleAsNewPattern', 'promisedSuite', '$compile', 'NodeService', '$location', '$timeout',
    function ($scope, $routeParams, $route, $log, DeleteParticularSuiteRun, ParticularRun, AcceptSampleAsNewPattern, promisedSuite, $compile, NodeService, $location, $timeout) {
        $scope.testSuiteID = promisedSuite.testSuiteID;
        $scope.runs = promisedSuite.runs;
        $scope.timestampToDate = timestampToDate;
        $scope.deleteSuiteRun = function (testSuiteRunID) {
            var promise =
                    DeleteParticularSuiteRun.deleteParticularSuiteRun(testSuiteRunID);
            promise.then(
                    function (payload) {
                        $log.info("reloaded");
                        $route.reload();
                        $log.info('suite run deleted sucessfully', payload);
                    },
                    function (errorPayload) {
                        $log.error('failure delete suite run', errorPayload);
                    });
        };
        $scope.first = promisedSuite.rootNode;
        $scope.testClass = "";
        $scope.first.index = -1000;
        $scope.selections = [];
        $scope.selections.push($scope.first);
        $scope.lastSelected = null;
        $scope.$on('select-change', function (event, data) {
            $scope.lastSelected = data;
            $timeout(function () {
                $scope.testClass = generateTestClass();

            });
        });
        $scope.filter = false;
        $scope.diffs = false;
        $scope.href = "";
        $scope.$watchGroup(['filter', 'testClass', 'diffs'], function (newVals, oldVals, $scope) {
            if (newVals !== oldVals) {
                if (newVals[0]) {
                    $scope.href = "?testClass=" + newVals[1] + "&diffsOnly=" + newVals[2];
                }
                else {
                    $scope.href = "";
                }
            }
        });
        $scope.$on('selections-splice', function (event, indexFromSplice) {
            $log.info("REGISTERED SPLICE");
            $log.info(indexFromSplice);
            $log.info($scope.selections);
            if ($scope.selections.length > 1) {
                $scope.selections.splice(indexFromSplice + 1);
            }
        });
        $scope.expandClass = function () {
            var value = $scope.lastSelected;
            var promisedNode = NodeService.query({nodeId: value,children: true}).$promise;
            promisedNode.then(function (resource) {
                $log.info(resource);
                if (resource.children.length > 0) {
                    $scope.selections.push(resource);
                    var index = $scope.selections.length - 1;
                    $(".form-group").last().after($compile('<div class="form-group" node-nav parent="selections[' + index + ']" style="float: left;" id="test-class' + index + '"></div>')($scope));
                }
            });
        };
        $scope.collapse = function () {
            var selections = $scope.selections;
            if (selections.length > 1) {
                $scope.$broadcast('collapse', selections[selections.length - 1].nodeId);
            }
        };
        $scope.acceptAllNewSamplesAsNewPatterns = function (testSuiteRunID) {
            var promised = ParticularRun.query({runId: testSuiteRunID}).$promise;
            promised.then(function (comparisonResults) {
                for (var i = 0; i < comparisonResults.length; i++) {
                    var result = comparisonResults[i];
                    $log.info(result);
                    if ($scope.isDiff(result)) {
                        AcceptSampleAsNewPattern.acceptSampleAsNewPattern(result.diffID);
                    }
                }
            });
        };
        var generateTestClass = function () {
            var selectedOptions = $(".form-control").children(":selected");
            var str = "";
            for (var i = 0; i < selectedOptions.length; i++) {
                str += selectedOptions[i].label;
                str += (i !== selectedOptions.length - 1) ? "." : "";
            }
            return str;
        };
    }


]);

visualTestingControllers.controller('ParticularRunCtrl', ['$scope', '$routeParams', '$log',
    '$route', '$location', 'RejectSample', 'AcceptSampleAsNewPattern', 'RejectPattern', 'AcceptNewMask', 'PatternService', 'ParticularSuite', 'DeleteSelectedMask', 'ParticularMask', 'UpdateSelectedMask', 'Masks', 'runs', '$window',
    function ($scope, $routeParams, $log, $route, $location, RejectSample, AcceptSampleAsNewPattern, RejectPattern, AcceptNewMask, PatternService, ParticularSuite, DeleteSelectedMask, ParticularMask, UpdateSelectedMask, Masks, runs, $window) {
        $scope.comparisonResults = runs;
        $scope.allResults = $scope.comparisonResults.length;
        $log.info($scope.comparisonResults.length);
        $scope.back = back;
        $scope.path = $location.path();
        $scope.visible = 0;
        $window.onscroll = function () {
            var currentTest = $(".jumbotron").filter(":in-viewport").parent();
            if ($(currentTest).get(0)) {
                var currentTestId = $(currentTest).attr("id").substr(9);
                var newVal = parseInt(currentTestId);
                if (newVal !== $scope.visible) {
                    $scope.visible = newVal;
                    $scope.$digest();
                }
            }
        };
        $scope.toggleSidebar = function () {
            $("#sidebar-arrow").toggleClass("toggled");
            $("#sidebar-wrapper").toggleClass("toggled");
        };
    }
]);

visualTestingControllers.controller('RunController', ['$scope', '$log', '$route'
            , 'ParticularSuite', 'PatternService'
            , 'Masks', 'AcceptNewMask', 'ParticularMask'
            , 'DeleteSelectedMask', 'UpdateSelectedMask'
            , 'AcceptSampleAsNewPattern', 'RejectPattern', 'RejectSample','Mask', function ($scope, $log, $route, ParticularSuite
                    , PatternService, Masks
                    , AcceptNewMask, ParticularMask
                    , DeleteSelectedMask, UpdateSelectedMask
                    , AcceptSampleAsNewPattern, RejectPattern, RejectSample,Mask) {
                $scope.jcropApi = null;
                $scope.masks = null;
                $scope.setCroppedImageAndAlignmentFromMask = function (maskObj) {
                    $log.info(maskObj);
                    var jcropApi = $scope.jcropApi;
                    var selection = jcropApi.ui.selection;
                    var imgSource = jcropApi.ui.stage.imgsrc;
                    var startX = selection.last.x;
                    var startY = selection.last.y;
                    var width = selection.last.w;
                    var height = selection.last.h;
                    var img = document.createElement("img");
                    img.width = imgSource.width;
                    img.height = imgSource.height;
                    var canvas = document.createElement("canvas");
                    canvas.width = img.width;
                    canvas.height = img.height;
                    var context = canvas.getContext("2d");
                    context.globalAlpha = 1;
                    context.fillStyle = "green";
                    context.rect(startX, startY, width, height);
                    context.fill();
                    var result = canvas.toDataURL();
                    maskObj.setSourceData(result);
                    maskObj.setTop(startY);
                    maskObj.setLeft(startX);
                    maskObj.setWidth(width);
                    maskObj.setHeight(height);
                };
                $scope.reloadJcrop = function () {
                    var jcropApi = $scope.jcropApi;
                    var wrapperContainer = jcropApi.container;
                    var children = $(wrapperContainer.get(0)).children(".jcrop-selection");
                    $(children).remove();
                    $scope.jcropApi.ui.multi.length = 0;
                    $scope.jcropApi.ui.multi = [];
                    var masks = $scope.masks;
                    for (var i = 0; i < masks.length; i++) {
                        var mask = masks[i];
                        var selection = jcropApi.newSelection();
                        selection.update($.Jcrop.wrapFromXywh([mask.left, mask.top, mask.width, mask.height]));
                        selection.maskID = mask.maskID;
                        var color = "#00ffd4";
                        selection.setColor(color).setOpacity(0.3);
                    }
                };
                $scope.acceptNewAlphaMask = function () {
                    $log.info($scope);
                    var promiseStart = Promise.resolve();
                    var promiseEnd = Promise.resolve();
                    var maskObj = new Mask();
                    var patternId = $scope.result.patternID;
                    var promisedTestSuite = ParticularSuite.query({testSuiteID: $route.current.params.testSuiteID}).$promise;
                    var promisedPattern = PatternService.query({patternID: patternId}).$promise;
                    promiseStart.then(function (value) {
                        $scope.setCroppedImageAndAlignmentFromMask(maskObj);
                        return promisedTestSuite;
                    }).
                            then(function (testSuite) {
                                maskObj.testSuiteName = testSuite.name;
                                return promisedPattern;
                            }).
                            then(function (value) {
                                maskObj.pattern = value;
                                return promiseEnd;
                            })
                            .then(function (value) {
                                $log.info(maskObj);
                                return AcceptNewMask.acceptNewMask(JSON.stringify(maskObj));
                            })
                            .then(function (value) {
                                return Masks.query({patternID: patternId}).$promise;
                            })
                            .then(function (masks) {
                                $scope.masks = masks;
                                $scope.reloadJcrop();
                            });
                };
                $scope.destroyCurrentMask = function () {
                    var jcropApi = $scope.jcropApi;
                    var selectedMaskId = jcropApi.ui.selection.maskID;
                    if (typeof selectedMaskId === 'undefined') {
                        $log.error("Mask to be destroyed not selected");
                    }
                    else {
                        DeleteSelectedMask.deleteSelectedMask(selectedMaskId).then(function (success) {
                            return Masks.query({patternID: $scope.result.patternID}).$promise;
                        }).then(function (newMasks) {
                            $scope.masks = newMasks;
                            $scope.reloadJcrop();
                            if (jcropApi.ui.multi.length === 0) {
                                $scope.clearJcrop();
                            }
                        });
                        /*jcropApi.deleteSelection();
                        $log.info(jcropApi);
                        $scope.reloadJcrop();
                        if (jcropApi.ui.multi.length === 0) {
                            $scope.clearJcrop();
                        }*/
                    }
                };
                $scope.updateSelectedMask = function () {
                    var selectedMaskId = $scope.jcropApi.ui.selection.maskID;
                    var patternId = $scope.result.patternID;
                    if (typeof selectedMaskId !== 'undefined') {
                        var promisedSelectedMask = ParticularMask.query({maskID: selectedMaskId}).$promise;
                        promisedSelectedMask.then(function (originalMask) {
                            $log.info(originalMask);
                            var obj = Mask.fromJson(originalMask);
                            $log.info(obj);
                            $scope.setCroppedImageAndAlignmentFromMask(obj);
                            return UpdateSelectedMask.updateSelectedMask(JSON.stringify(obj));
                        }).then(function (succesPayload) {
                            var masks = Masks.query({patternID: patternId});
                            return masks.$promise;
                        })
                                .then(function (masks) {
                                    $scope.masks = masks;
                                    $scope.reloadJcrop();

                                });
                    }
                    else {
                        $log.error("Mask not selected");
                    }

                };
                $scope.clearJcrop = function () {
                    var jcropContainer = $scope.jcropApi.container;
                    var jcropShades = $(jcropContainer).children(".jcrop-shades").children("div");
                    $log.info(jcropShades);
                    $(jcropShades).css("background-color", "transparent");
                };
                $scope.$on('masks-created', function (event, data) {
                    $scope.masks = data;
                });
                $scope.$on('jcrop-api-created', function (event, data) {
                    $scope.jcropApi = data;
                });
                $scope.rejectPattern = function () {
                    var diffID = $scope.result.diffID;
                    var promise = RejectPattern.rejectPattern(diffID);
                    promise.then(function (payload) {
                        $route.reload();
                        $log.info('pattern deleted succesfully', payload);
                    }, function (errorPayload) {
                        $log.error('failure when deleting pattern', errorPayload);
                    });
                };
                $scope.rejectSample = function () {
                    var diffID = $scope.result.diffID;
                    var promise =
                            RejectSample.rejectSample(diffID);
                    promise.then(
                            function (payload) {
                                /*if ($scope.comparisonResults.length == 1) {
                                 back();
                                 } else {
                                 $route.reload();
                                 }*/
                                $route.reload();
                                $log.info('suite deleted sucessfully', payload);
                            },
                            function (errorPayload) {
                                $log.error('failure delete suite', errorPayload);
                            });
                };

                $scope.acceptSampleAsNewPattern = function () {
                    var diffID = $scope.result.diffID;
                    var promise = AcceptSampleAsNewPattern.acceptSampleAsNewPattern(diffID);
                    promise.then(function (payload) {
                        $route.reload();
                        $log.info('pattern replaced succesfully', payload);
                    }, function (errorPayload) {
                        $log.error('failure when replacing pattern', errorPayload);
                    });
                };
            }]);

/* Help methods */
var timestampToDate = function (timestamp) {
    var date = new Date(timestamp);

    var hours = date.getHours();
    var minutes = "0" + date.getMinutes();
    var seconds = "0" + date.getSeconds();
    var time = hours + ':' + minutes.substr(minutes.length - 2) + ':' + seconds.substr(seconds.length - 2);

    var monthNames = ["January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"];

    return monthNames[date.getMonth()] + " " + date.getDate() + ", " + date.getFullYear() + ", " + time;
};

var back = function () {
    window.history.back();
};
