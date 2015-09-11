/* global Promise */

'use strict';

/* Services */

var visualTestingServices = angular.module('visualTestingServices', ['ngResource']);

visualTestingServices.factory('Suites', ['$resource',
    function ($resource) {
        return $resource('rest/suites', {}, {
            query: {method: 'GET', isArray: true}
        });
    }]);

visualTestingServices.factory('ParticularSuite', ['$resource',
    function ($resource) {
        return $resource('rest/suites/:testSuiteID', {testSuiteID: '@testSuiteID'}, {
            query: {method: 'GET', isArray: false}
        });
    }]);

visualTestingServices.factory('ParticularRun', ['$resource', '$log',
    function ($resource, $log) {
        return $resource('rest/runs/comparison-result/:runId', {runId: '@runId'}, {
            query: {method: 'GET', isArray: true}
        });
    }]);

visualTestingServices.factory('DeleteParticularSuite',
        function ($http) {
            return {
                deleteParticularSuite: function (testSuiteID) {
                    return $http.delete('rest/suites/' + testSuiteID);
                }
            }
        });

visualTestingServices.factory('DeleteParticularSuiteRun',
        function ($http) {
            return {
                deleteParticularSuiteRun: function (testSuiteRunID) {
                    return $http.delete('rest/runs/' + testSuiteRunID);
                }
            }
        });

visualTestingServices.factory('RejectPattern', function ($http) {
    return {
        rejectPattern: function (diffID) {
            return $http.put('rest/patterns/reject/' + diffID);
        }
    }
});

visualTestingServices.factory('RejectSample', function ($http) {
    return {
        rejectSample: function (diffID) {
            return $http.put('rest/samples/reject/' + diffID);
        }
    }
});

visualTestingServices.factory('AcceptSampleAsNewPattern', function ($http) {
    return {
        acceptSampleAsNewPattern: function (diffID) {
            return $http.put('rest/patterns/update/' + diffID);
        }
    }
});

visualTestingServices.factory('AcceptNewMask', function ($http) {
    return {
        acceptNewMask: function (jsonMask) {
            console.log(jsonMask);
            return $http.post('rest/masks', jsonMask);
        }
    }
});

visualTestingServices.factory('ParticularSample', ['$resource', function ($resource) {
        return $resource('rest/samples/:sampleID', {sampleID: '@sampleID'}, {query: {method: 'GET', isArray: false}});
    }
]);

visualTestingServices.factory('ResolveRuns', ['$route', 'ParticularSuite', 'ParticularRun', '$q', '$log', function ($route, ParticularSuite, ParticularRun, $q, $log) {
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

        var updatePercentageOneRun = function (run) {
            run.successfulPercentage = getSuccessfulPercentage(run);
            run.failedPercentage = getFailedPercentage(run);
            run.failedTestPercentage = getFailedTestsPercentage(run);
        };

        var needsToBeUpdatedOneComparisonResult = function (comparisonResult) {
            var patternModificationDate = parseInt(comparisonResult.patternModificationDate);
            var sampleModificationDate = parseInt(comparisonResult.sampleModificationDate);
            if (patternModificationDate > sampleModificationDate) {
                return true;
            }
            return false;
        };

        var updateNeedsToBeUpdatedOneRun = function (run) {
            var comparisonResultsPromised = ParticularRun.query({runId: run.testSuiteRunID}).$promise;
            var resultPromised = false;
            comparisonResultsPromised.then(function (comparisonResultsResolved) {
                for (var i = 0; i < comparisonResultsResolved.length; i++) {
                    $log.info("UPDATED PROMISE");
                    var comparisonResult = comparisonResultsResolved[i];
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
            });
            return resultPromised;
        };

        var getPromisedSuite = function () {
            var toBeResolvedSuite = ParticularSuite.query({testSuiteID: $route.current.params.testSuiteID}).$promise;
            toBeResolvedSuite.then(function (successValue) {
                $log.info(successValue);
                var promisedRuns = successValue.runs;
                for (var i = 0; i < promisedRuns.length; i++) {
                    var currentRun = promisedRuns[i];
                    updatePercentageOneRun(currentRun);
                    var currentNumberOfTests = getSumOfTests(currentRun);
                    var previousNumberOfTests = getSumOfTests(promisedRuns[i - 1]);
                    if (currentNumberOfTests > previousNumberOfTests && (i > 0)) {
                        currentRun.extraTests = currentNumberOfTests - previousNumberOfTests;
                    }
                    currentRun.needsToBeUpdated = updateNeedsToBeUpdatedOneRun(currentRun);
                }

            });
            return toBeResolvedSuite;
        };

        return {
            getRuns: function () {
                return getPromisedSuite();
            }


        }
    }]);