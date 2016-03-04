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
        $scope.getFailedPercentage = getFailedPercentage;
        $scope.getSuccessfulPercentage = getSuccessfulPercentage;
        $scope.getFailedTestsPercentage = getFailedTestsPercentage;
    }]);

visualTestingControllers.controller('ParticularSuiteCtrl', ['$scope', '$routeParams',
    '$route', '$log', 'DeleteParticularSuiteRun', 'ParticularRun', 'AcceptSampleAsNewPattern', 'promisedSuite',
    function ($scope, $routeParams, $route, $log, DeleteParticularSuiteRun, ParticularRun, AcceptSampleAsNewPattern, promisedSuite) {
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
        $scope.isDiff = isDiff;
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



    }


]);

visualTestingControllers.controller('ParticularRunCtrl', ['$scope', '$routeParams', '$log',
    '$route', '$location', 'RejectSample', 'AcceptSampleAsNewPattern', 'RejectPattern', 'AcceptNewMask', 'PatternService', 'ParticularSuite', 'DeleteSelectedMask', 'ParticularMask', 'UpdateSelectedMask', 'Masks', 'promisedRuns', '$window',
    function ($scope, $routeParams, $log, $route, $location, RejectSample, AcceptSampleAsNewPattern, RejectPattern, AcceptNewMask, PatternService, ParticularSuite, DeleteSelectedMask, ParticularMask, UpdateSelectedMask, Masks, promisedRuns, $window) {
        $scope.comparisonResults = promisedRuns;
        $scope.allResults = $scope.comparisonResults.length;
        $log.info($scope.comparisonResults.length);
        $scope.back = back;
        $scope.path = $location.path();
        $scope.acceptNewAlphaMask = function (patternId) {
            var jcropApi = _.find($scope.comparisonResults, function (comparisonResult) {
                return comparisonResult.patternID === patternId;
            }).jcrop_api;
            $log.info(jcropApi);
            var promiseStart = Promise.resolve();
            var promiseEnd = Promise.resolve();
            var maskObj = {};
            maskObj.maskID = null;
            var promisedTestSuite = ParticularSuite.query({testSuiteID: $route.current.params.testSuiteID}).$promise;
            var promisedPattern = PatternService.query({patternID: patternId}).$promise;
            promiseStart.then(function (value) {
                $scope.setCroppedImageAndAlignmentFromMask(jcropApi, maskObj);
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
                        $scope.reloadJcrop(jcropApi, masks);
                    });


        };

        $scope.setCroppedImageAndAlignmentFromMask = function (jcropApi, maskObj) {
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
            $log.info(result);
            maskObj.sourceData = result;
            maskObj.horizontalAlignment = null;
            maskObj.verticalAlignment = null;
            maskObj.top = startY;
            maskObj.left = startX;
            maskObj.width = width;
            maskObj.height = height;
        };
        $scope.visible = 0;
        $window.onscroll = function () {
            var currentTest = $(".jumbotron").filter(":in-viewport").parent();
            if ($(currentTest).get(0)) {
                var currentTestId = $(currentTest).attr("id").substr(9);
                var newVal = parseInt(currentTestId);
                if (newVal !== $scope.visible) {
                    $scope.visible = newVal;
                    $scope.$apply();
                }
            }
        };
        $scope.reloadJcrop = function (jcropApi, masks) {
            var wrapperContainer = jcropApi.container;
            var children = $(wrapperContainer.get(0)).children(".jcrop-selection");
            $(children).remove();
            jcropApi.ui.multi.length = 0;
            jcropApi.ui.multi = [];
            for (var i = 0; i < masks.length; i++) {
                var mask = masks[i];
                var selection = jcropApi.newSelection();
                $log.info(selection);
                selection.update($.Jcrop.wrapFromXywh([mask.left, mask.top, mask.width, mask.height]));
                selection.maskID = mask.maskID;
                selection.setColor("#00ffd4", 0.3);
            }
        };

        $scope.updateSelectedMask = function (patternId) {
            /*var clicked = event.target;
             var parentDiv = $(clicked).parents().get(1);
             var img = $(parentDiv).find('img.jcrop').get(0);
             var sampleId = parseInt($(img).attr("sampleid"));*/
            var selectedComparisonResult = _.find($scope.comparisonResults, function (comparisonResult) {
                return comparisonResult.patternID === patternId;
            });
            var selectedJcropApi = selectedComparisonResult.jcrop_api;
            var selectedMaskId = selectedJcropApi.ui.selection.maskID;
            if (typeof selectedMaskId !== 'undefined') {
                var promisedSelectedMask = ParticularMask.query({maskID: selectedMaskId}).$promise;
                promisedSelectedMask.then(function (originalMask) {
                    var maskObj = originalMask;
                    $scope.setCroppedImageAndAlignmentFromMask(selectedJcropApi, maskObj);
                    $log.info(maskObj);
                    return UpdateSelectedMask.updateSelectedMask(JSON.stringify(maskObj));
                }).then(function (succesPayload) {
                    var masks = Masks.query({patternID: patternId});
                    return masks.$promise;
                })
                        .then(function (allMasks) {
                            $log.info("MASKS");
                            $log.info(allMasks);
                            $scope.reloadJcrop(selectedJcropApi, allMasks);

                        });
            }
            else {
                $log.error("Mask not selected");
            }

        };
        $scope.rejectPattern = function (diffID) {
            var promise = RejectPattern.rejectPattern(diffID);
            promise.then(function (payload) {
                $route.reload();
                $log.info('pattern deleted succesfully', payload);
            }, function (errorPayload) {
                $log.error('failure when deleting pattern', errorPayload);
            });
        };
        $scope.rejectSample = function (diffID) {
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

        $scope.acceptSampleAsNewPattern = function (diffID) {
            var promise = AcceptSampleAsNewPattern.acceptSampleAsNewPattern(diffID);
            promise.then(function (payload) {
                $route.reload();
                $log.info('pattern replaced succesfully', payload);
            }, function (errorPayload) {
                $log.error('failure when replacing pattern', errorPayload);
            });
        };

        $scope.destroyCurrentMask = function (patternId) {
            var jcropApi;
            jcropApi = _.find($scope.comparisonResults, function (comparisonResult) {
                return comparisonResult.patternID === patternId;
            }).jcrop_api;
            var selectedMaskId = jcropApi.ui.selection.maskID;
            if (typeof selectedMaskId === 'undefined') {
                $log.error("Mask to be destroyed not selected");
            }
            else {
                DeleteSelectedMask.deleteSelectedMask(selectedMaskId);
                jcropApi.deleteSelection();
                $log.info(jcropApi);
                if (jcropApi.ui.multi.length === 0) {
                    $scope.clearJcrop(jcropApi);
                }
            }
        };

        $scope.clearJcrop = function (jcropApi) {
            var jcropContainer = jcropApi.container;
            var jcropShades = $(jcropContainer).children(".jcrop-shades").children("div");
            $log.info(jcropShades);
            $(jcropShades).css("background-color", "transparent");
        };

        $scope.toggleSidebar = function () {
            $("#sidebar-arrow").toggleClass("toggled");
            $("#sidebar-wrapper").toggleClass("toggled");
        };
    }
]);

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

var getSumOfTests = function (run) {
    if (!isRun(run)) {
        return 0;
    }
    else {
        var success = run.numberOfSuccessfulComparisons;
        var failed = run.numberOfFailedComparisons;
        var failedTests = run.numberOfFailedFunctionalTests;
        var sum = success + failed + failedTests;
        return sum;
    }
};

var getSuccessfulPercentage = function (run) {
    return 100 * (run.numberOfSuccessfulComparisons / getSumOfTests(run));
};

var getFailedPercentage = function (run) {
    return 100 * (run.numberOfFailedComparisons / getSumOfTests(run));
};

var getFailedTestsPercentage = function (run) {
    return 100 * (run.numberOfFailedFunctionalTests / getSumOfTests(run));
};

var isRun = function (run) {
    if (run === null || angular.isUndefined(run)) {
        return false;
    }
    var success = run.numberOfSuccessfulComparisons;
    var failed = run.numberOfFailedComparisons;
    var failedTests = run.numberOfFailedFunctionalTests;
    var sum = success + failed + failedTests;
    return sum !== 0;
};

var isDiff = function (result) {
    return result.diffUrl !== null;
};

