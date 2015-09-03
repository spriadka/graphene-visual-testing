/* global angular, Promise */

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
    '$route', '$log', 'ParticularSuite', 'DeleteParticularSuiteRun', 'ParticularRun', 'AcceptSampleAsNewPattern', '$q',
    function ($scope, $routeParams, $route, $log, ParticularSuite, DeleteParticularSuiteRun, ParticularRun, AcceptSampleAsNewPattern, $q) {
        $scope.particularSuite = ParticularSuite.query({testSuiteID: $routeParams.testSuiteID});
        $scope.runs;
        var needsToBeUpdatedOneComparisonResult = function (comparisonResult) {
            var patternModificationDate = parseInt(comparisonResult.patternModificationDate);
            var sampleModificationDate = parseInt(comparisonResult.sampleModificationDate);
            if (patternModificationDate > sampleModificationDate) {
                return true;
            }
            return false;
        };
        var updateNeedsToBeUpdatedOneRun = function (run) {
            $log.info("RUNNING NEEDS TO BE UPDATED");
            var result;
            var promised = ParticularRun.query({runId: run.testSuiteRunID}).$promise;
            promised.then(function(comparisonResults){
                var resultPromised = false;
                for (var i = 0; i < comparisonResults.length; i++) {
                    $log.info("UPDATED PROMISE");
                    var comparisonResult = comparisonResults[i];
                    var partialResult = needsToBeUpdatedOneComparisonResult(comparisonResult);
                    if (partialResult) {
                        run.errorContent = (typeof run.errorContent !== 'undefined' && run.errorContent instanceof Array) ? run.errorContent : [];
                        var errorData = {};
                        errorData.name = comparisonResult.testName;
                        errorData.patternDate = comparisonResult.patternModificationDate;
                        errorData.sampleDate = comparisonResult.sampleModificationDate;
                        run.errorContent.push(errorData);
                    }
                    resultPromised = resultPromised || partialResult;
                }
                run.needsToBeUpdated = resultPromised;
            });
        };

        var updatePercentageOneRun = function (run) {
            run.successfulPercentage = getSuccessfulPercentage(run);
            run.failedPercentage = getFailedPercentage(run);
            run.failedTestPercentage = getFailedTestsPercentage(run);
        };

        $scope.addRemainingInfoToRuns = function () {
            var promised = $scope.particularSuite.$promise;
            promised.then(function (value) {
                var allRuns = value.runs;
                for (var i = 0; i < allRuns.length; i++) {
                    $log.info("PROCESSING RUN: " + i);
                    var currentRun = allRuns[i];
                    var currentNumberOfTests = getSumOfTests(currentRun);
                    var previousNumberOfTests = getSumOfTests(allRuns[i - 1]);
                    if (currentNumberOfTests > previousNumberOfTests && (i > 0)) {
                        currentRun.extraTests = currentNumberOfTests - previousNumberOfTests;
                    }
                    updatePercentageOneRun(currentRun);
                    updateNeedsToBeUpdatedOneRun(currentRun);
                }
                return value;
            }).then(function(value){
                $log.info(value);
                $scope.runs = value.runs;
                $log.info("RUNS IN SCOPE");
                
            });
        };
        $scope.addRemainingInfoToRuns();
        $scope.timestampToDate = timestampToDate;
        $scope.count = 0;
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
    '$route', '$location', 'ParticularRun', 'RejectSample', 'AcceptSampleAsNewPattern', 'RejectPattern',
    function ($scope, $routeParams, $log, $route, $location, ParticularRun,
            RejectSample, AcceptSampleAsNewPattern, RejectPattern) {

        $log.info("Controller called");
        $scope.comparisonResults = ParticularRun.query({runId: $routeParams.runId});
        $scope.back = back;
        $scope.acceptNewAlphaMask = function(event){
            var clicked = event.target;
            var parentDiv = $(clicked).parents().get(1);
            var masks = $(parentDiv).find('.jcrop-selection').get(0);
            var img = $(parentDiv).find('img[jcrop]').get(0);
            var base64Image = $scope.getCroppedImageFromMask(masks,img);
            $log.info(base64Image);
        };
        
        $scope.getCroppedImageFromMask = function(mask,img){
            var startX = $(mask).css("left");
            startX = startX.substring(0,startX.length - 2);
            var startY = $(mask).css("top");
            startY = startY.substring(0,startY.length - 2);
            var width = $(mask).css("width");
            width = width.substring(0,width.length - 2);
            var height = $(mask).css("height");
            height = height.substring(0,height.length - 2);
            var canvas = document.createElement('canvas');
            canvas.setAttribute('width',width);
            canvas.setAttribute('height',height);
            var context = canvas.getContext("2d");
            context.drawImage(img,parseInt(startX),parseInt(startY),parseInt(width),parseInt(height),0,0,parseInt(width),parseInt(height));
            var result = canvas.toDataURL();
            return result;
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

