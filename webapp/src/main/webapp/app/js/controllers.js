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
    '$route', '$location','RejectSample', 'AcceptSampleAsNewPattern', 'RejectPattern', 'AcceptNewMask', 'ParticularSample', 'ParticularSuite', 'DeleteSelectedMask', 'ParticularMask', 'UpdateSelectedMask', 'Masks','promisedRuns',
    function ($scope, $routeParams, $log, $route, $location,RejectSample, AcceptSampleAsNewPattern, RejectPattern, AcceptNewMask, ParticularSample, ParticularSuite, DeleteSelectedMask, ParticularMask, UpdateSelectedMask, Masks,promisedRuns) {
        $scope.comparisonResults = promisedRuns;
        $scope.back = back;
        $scope.path = $location.path();
        $scope.acceptNewAlphaMask = function (event) {
            var clicked = event.target;
            var parentDiv = $(clicked).parents().get(1);
            var img = $(parentDiv).find('img.jcrop').get(0);
            var sampleId = parseInt($(img).attr("sampleid"));
            var jcropApi = _.find($scope.comparisonResults, function (comparisonResult) {
                return comparisonResult.sampleID === sampleId;
            }).jcrop_api;
            $log.info(jcropApi);
            var promiseStart = Promise.resolve();
            var promiseEnd = Promise.resolve();
            var maskObj = {};
            maskObj.maskID = null;
            var promisedTestSuite = ParticularSuite.query({testSuiteID: $route.current.params.testSuiteID}).$promise;
            var promisedSample = ParticularSample.query({sampleID: sampleId}).$promise;
            promiseStart.then(function (value) {
                $scope.setCroppedImageAndAlignmentFromMask(jcropApi, maskObj);
                        return promisedTestSuite;
                    }).
                    then(function(testSuite){
                        maskObj.testSuiteName = testSuite.name;
                        return promisedSample;
                    }).
                    then(function (value) {
                        maskObj.sample = value;
                        return promiseEnd;
                    })
                    .then(function (value) {
                        $log.info(maskObj);
                        return AcceptNewMask.acceptNewMask(JSON.stringify(maskObj));
                    })
                    .then(function (value) {
                        return Masks.query({sampleID: sampleId}).$promise;
                    })
                    .then(function (masks) {
                        $scope.reloadJcrop(jcropApi, masks);
                    });


        };

        $scope.setCroppedImageAndAlignmentFromMask = function (jcropApi, maskObj) {
            var selection = jcropApi.ui.selection;
            var img = jcropApi.ui.stage.imgsrc;
            var startX = selection.last.x;
            $log.info(typeof startX);
            var startY = selection.last.y;
            var width = selection.last.w;
            var height = selection.last.h;
            var canvas = document.createElement("canvas");
            canvas.width = width;
            canvas.height = height;
            var context = canvas.getContext("2d");
            context.drawImage(img, startX, startY, width, height, 0, 0, width, height);
            var result = canvas.toDataURL();
            maskObj.sourceData = result;
            maskObj.horizontalAlignment = null;
            maskObj.verticalAlignment = null;
            maskObj.top = startY;
            maskObj.left = startX;
            maskObj.width = width;
            maskObj.height = height;
        };

        $scope.reloadJcrop = function (jcropApi, masks) {
            $('.jcrop-selection').remove();
            jcropApi.ui.multi = [];
            for (var i = 0; i < masks.length; i++) {
                var mask = masks[i];
                var selection = jcropApi.newSelection();
                $log.info(selection);
                selection.update($.Jcrop.wrapFromXywh([mask.left, mask.top, mask.width, mask.height]));
                selection.maskID = mask.maskID;
                selection.setColor("#00ffd4",0.3);
            }
        };

        $scope.updateSelectedMask = function (event) {
            var clicked = event.target;
            var parentDiv = $(clicked).parents().get(1);
            var img = $(parentDiv).find('img.jcrop').get(0);
            var sampleId = parseInt($(img).attr("sampleid"));
            var selectedComparisonResult = _.find($scope.comparisonResults, function (comparisonResult) {
                return comparisonResult.sampleID === sampleId;
            });
            var selectedJcropApi = selectedComparisonResult.jcrop_api;
            $log.info("BEFORE CHANGE");
            $log.info(selectedJcropApi);
            var selectedMaskId = selectedJcropApi.ui.selection.maskID;
            if (typeof selectedMaskId !== 'undefined') {
                var promisedSelectedMask = ParticularMask.query({maskID: selectedMaskId}).$promise;
                promisedSelectedMask.then(function (originalMask) {
                    var maskObj = originalMask;
                    $scope.setCroppedImageAndAlignmentFromMask(selectedJcropApi, maskObj);
                    $log.info(maskObj);
                    return UpdateSelectedMask.updateSelectedMask(JSON.stringify(maskObj));
                }).then(function (succesPayload) {
                    var masks = Masks.query({sampleID: sampleId});
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

        $scope.destroyCurrentMask = function (event) {
            var clicked = event.target;
            var parentDiv = $(clicked).parents().get(1);
            var img = $(parentDiv).find('img.jcrop').get(0);
            var sampleId = parseInt($(img).attr("sampleid"));
            var jcropApi;
            jcropApi = _.find($scope.comparisonResults, function (comparisonResult) {
                return comparisonResult.sampleID === sampleId;
            }).jcrop_api;
            var selectedMaskId = jcropApi.ui.selection.maskID;
            $log.info(selectedMaskId);
            $log.info(typeof selectedMaskId);
            if (typeof selectedMaskId === 'undefined') {
                $log.error("Mask to be destroyed not selected");
            }
            else {
                DeleteSelectedMask.deleteSelectedMask(selectedMaskId);
                jcropApi.deleteSelection();
                $log.info(jcropApi);
                if (jcropApi.ui.multi.length === 0){
                    jcropApi.destroy();
                }
            }
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

