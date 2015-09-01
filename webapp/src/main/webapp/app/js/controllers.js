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
        var needsToBeUpdatedOneRun = function (comparisonResult) {
            var patternModificationDate = parseInt(comparisonResult.patternModificationDate);
            var sampleModificationDate = parseInt(comparisonResult.sampleModificationDate);
            if (patternModificationDate > sampleModificationDate) {
                return true;
            }
            return false;
        };
        $scope.needsToBeUpdated = function (run) {
            $log.info("RUNNING NEEDS TO BE UPDATED");
            var promised = ParticularRun.query({runId: run.testSuiteRunID}).$promise;
            promised.then(function (comparisonResults) {
                var resultPromised = false;
                for (var i = 0; i < comparisonResults.length; i++) {
                    $log.info("UPDATED PROMISE");
                    var comparisonResult = comparisonResults[i];
                    var partialResult = needsToBeUpdatedOneRun(comparisonResult);
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
                    currentRun.successfulPercentage = getSuccessfulPercentage(currentRun);
                    currentRun.failedPercentage = getFailedPercentage(currentRun);
                    currentRun.failedTestPercentage = getFailedTestsPercentage(currentRun);
                    $scope.needsToBeUpdated(currentRun);
                }
                $scope.particularSuite.runs = allRuns;
                return allRuns;
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
        $scope.getFailedPercentage = getFailedPercentage;
        $scope.getSuccessfullPercentage = getSuccessfulPercentage;
        $scope.getFailedTestsPercentage = getFailedTestsPercentage;
        $scope.isDiff = isDiff;
        $scope.acceptAllNewSamplesAsNewPatterns = function (testSuiteRunID) {
            var promised = ParticularRun.query({runId: testSuiteRunID}).$promise;
            promised.then(function (value) {
                var jsonStringvalue = angular.toJson(value);
                var comparisonResults = JSON.parse(jsonStringvalue);
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

        $scope.comparisonResults = ParticularRun.query({runId: $routeParams.runId});
        $scope.back = back;
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

        $scope.isDiff = isDiff;
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
    var success = run.numberOfSuccessfullComparisons;
    var failed = run.numberOfFailedComparisons;
    var failedTests = run.numberOfFailedFunctionalTests;
    var sum = success + failed + failedTests;
    return 100 * (success / sum);
};

var getFailedPercentage = function (run) {
    var success = run.numberOfSuccessfullComparisons;
    var failed = run.numberOfFailedComparisons;
    var failedTests = run.numberOfFailedFunctionalTests;
    var sum = success + failed + failedTests;
    return 100 * (failed / sum);
};

var getFailedTestsPercentage = function (run) {
    var success = run.numberOfSuccessfullComparisons;
    var failed = run.numberOfFailedComparisons;
    var failedTests = run.numberOfFailedFunctionalTests;
    var sum = success + failed + failedTests;
    return 100 * (failedTests / sum);
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
    } else {
        return false;
    }
};

