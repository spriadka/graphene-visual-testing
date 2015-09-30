/* global angular, Promise, Integer */

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
        $log.info($scope.suites);
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
        $scope.getSuccessfullPercentage = getSuccessfulPercentage;
        $scope.getFailedTestsPercentage = getFailedTestsPercentage;
    }]);

visualTestingControllers.controller('ParticularSuiteCtrl', ['$scope', '$routeParams',
    '$route', '$log', 'DeleteParticularSuiteRun', 'ParticularRun', 'AcceptSampleAsNewPattern','promisedSuite',
    function ($scope, $routeParams, $route, $log,DeleteParticularSuiteRun, ParticularRun, AcceptSampleAsNewPattern,promisedSuite) {
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
    '$route', '$location', 'ParticularRun', 'RejectSample', 'AcceptSampleAsNewPattern', 'RejectPattern', 'AcceptNewMask', 'ParticularSample', 'ParticularSuite','DeleteSelectedMask','ParticularMask','UpdateSelectedMask',
    function ($scope, $routeParams, $log, $route, $location, ParticularRun,
            RejectSample, AcceptSampleAsNewPattern, RejectPattern, AcceptNewMask, ParticularSample, ParticularSuite,DeleteSelectedMask,ParticularMask,UpdateSelectedMask) {

        $scope.comparisonResults = ParticularRun.query({runId: $routeParams.runId});
        $scope.back = back;
        $scope.acceptNewAlphaMask = function (event) {
            var clicked = event.target;
            var parentDiv = $(clicked).parents().get(1);
            var canvas = $(parentDiv).find('canvas').get(0);
            var masks = $(parentDiv).find('.jcrop-selection.jcrop-current').get(0);
            $log.info(parentDiv);
            $log.info(masks);
            if (typeof masks !== 'undefined') {
                var promiseStart = Promise.resolve();
                var promiseEnd = Promise.resolve();
                var img = $(parentDiv).find('img[jcrop]').get(0);
                var maskObj = {};
                var promisedSample = ParticularSample.query({sampleID: img.id}).$promise;
                var promisedTestSuite = ParticularSuite.query({testSuiteID: $routeParams.testSuiteID}).$promise;
                promiseStart.then(function (value) {
                    $scope.setCroppedImageAndAlignmentFromMask(masks, img, maskObj);
                    return promisedTestSuite;
                }).
                        then(function (value) {
                            maskObj.testSuite = value;
                            return promisedSample;
                        }).
                        then(function (value) {
                            maskObj.sample = value;
                            return promiseEnd;
                        })
                        .then(function (value) {
                            AcceptNewMask.acceptNewMask(JSON.stringify(maskObj));
                        });
            }

        };

        $scope.setCroppedImageAndAlignmentFromMask = function (maskElem, img, maskObj) {
            var startX = $(maskElem).css("left");
            startX = parseInt(startX.substring(0, startX.length - 2));
            var startY = $(maskElem).css("top");
            startY = parseInt(startY.substring(0, startY.length - 2));
            var width = $(maskElem).css("width");
            width = parseInt(width.substring(0, width.length - 2));
            var height = $(maskElem).css("height");
            height = parseInt(height.substring(0, height.length - 2));
            var canvas = document.createElement('canvas');
            canvas.setAttribute('width', width.toString());
            canvas.setAttribute('height', height.toString());
            var context = canvas.getContext("2d");
            context.drawImage(img, startX, startY, width, height, 0, 0, width, height);
            var result = canvas.toDataURL();
            maskObj.sourceData = result;
            maskObj.horizontalAlignment = null;
            maskObj.verticalAlignment = null;
            maskObj.top = startY;
            maskObj.left = startY;
            maskObj.width = width;
            maskObj.height = height;
        };

        $scope.updateComparisonResults = function () {
            $log.info("UPDATERUNS");
            var promisedComparisonResults = $scope.comparisonResults.$promise;
            promisedComparisonResults.then(function (value) {
                for (var i = 0; i < value.length; i++) {
                    var comparisonResult = value[i];
                    comparisonResult.isDiff = isDiff(comparisonResult);
                }
                $log.info(value);
                $log.info($scope.comparisonResults);
            });
        };
        $scope.updateSelectedMask = function(event){
            var clicked = event.target;
            var parentDiv = $(clicked).parents().get(1);
            var img = $(parentDiv).find('img[jcrop]').get(0);
            var selectedMaskElement = $(parentDiv).find('.jcrop-selection.jcrop-current').get(0);
            var selectedMaskId = parseInt($(selectedMaskElement).attr("maskid"));
            var promisedSelectedMask = ParticularMask.query({maskID: selectedMaskId}).$promise;
            promisedSelectedMask.then(function(originalMask){
                var maskObj = originalMask;
                $log.info(maskObj);
                $scope.setCroppedImageAndAlignmentFromMask(selectedMaskElement,img,maskObj);
                $log.info(maskObj);
                UpdateSelectedMask.updateSelectedMask(JSON.stringify(maskObj));
            });
            
        };
        $scope.updateComparisonResults();
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
        
        $scope.destroyCurrentMask = function(event){
            var clicked = event.target;
            var parentDiv = $(clicked).parents().get(1);
            var selectedMask = $(parentDiv).find('.jcrop-selection.jcrop-current').get(0);
            var selectedMaskId = parseInt($(selectedMask).attr("maskid"));
            $log.info(selectedMaskId);
            $log.info(typeof selectedMaskId);
            DeleteSelectedMask.deleteSelectedMask(selectedMaskId);
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

var getSumOfTests = function (run) {
    if (!isRun(run)) {
        return 0;
    }
    else {
        var success = run.numberOfSuccessfullComparisons;
        var failed = run.numberOfFailedComparisons;
        var failedTests = run.numberOfFailedFunctionalTests;
        var sum = success + failed + failedTests;
        return sum;
    }
};

var getSuccessfulPercentage = function (run) {
    return 100 * (run.numberOfSuccessfullComparisons / getSumOfTests(run));
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
    var success = run.numberOfSuccessfullComparisons;
    var failed = run.numberOfFailedComparisons;
    var failedTests = run.numberOfFailedFunctionalTests;
    var sum = success + failed + failedTests;
    if (sum === 0) {
        return false;
    }
    return true;
};

var isDiff = function (result) {
    if (result.diffUrl !== null) {
        return true;
    }
    return false;
};

