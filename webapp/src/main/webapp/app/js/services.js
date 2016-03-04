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

visualTestingServices.factory('Masks', ['$resource', function ($resource) {
        return $resource('rest/masks/pattern/:patternID', {patternID: '@patternID'}, {
            query: {method: 'GET', isArray: true}
        });
    }]);

visualTestingServices.factory('ParticularMask', ['$resource', function ($resource) {
        return $resource('rest/masks/:maskID', {maskID: '@maskID'}, {
            query: {method: 'GET', isArray: false}
        });
    }]);

visualTestingServices.factory('ParticularRun', ['$resource',
    function ($resource) {
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
            return $http.post('rest/masks', jsonMask);
        }
    }
});

visualTestingServices.factory('DeleteSelectedMask', function ($http) {
    return {
        deleteSelectedMask: function (selectedMaskId) {
            return $http.delete('rest/masks/' + selectedMaskId);
        }
    }
});

visualTestingServices.factory('UpdateSelectedMask', function ($http) {
    return {
        updateSelectedMask: function (selectedMask) {
            return $http.put('rest/masks/', selectedMask);
        }
    }
});

visualTestingServices.factory('PatternService', ['$resource', function ($resource) {
        return $resource('rest/patterns/:patternID', {patternID: '@patternID'}, {query: {method: 'GET', isArray: false}});
    }]);

visualTestingServices.factory('ParticularSample', ['$resource', function ($resource) {
        return $resource('rest/samples/:sampleID', {sampleID: '@sampleID'}, {query: {method: 'GET', isArray: false}});
    }
]);

visualTestingServices.factory('ResolveSuite', ['$route', 'ParticularSuite', 'ParticularRun', '$q', '$log', function ($route, ParticularSuite, ParticularRun, $q, $log) {
        var getSumOfTests = function (run) {
            console.log("IS RUN? " + isRun(run));
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

        var updatePercentageOneRun = function (run) {
            run.successfulPercentage = getSuccessfulPercentage(run);
            run.failedPercentage = getFailedPercentage(run);
            run.failedTestPercentage = getFailedTestsPercentage(run);
        };

        var needsToBeUpdatedOneComparisonResult = function (comparisonResult) {
            var patternModificationDate = parseInt(comparisonResult.patternModificationDate);
            var sampleModificationDate = parseInt(comparisonResult.sampleModificationDate);
            if (patternModificationDate >= sampleModificationDate) {
                return true;
            }
            return false;
        };

        var updateNeedsToBeUpdatedOneRun = function (run) {
            var comparisonResultsPromised = ParticularRun.query({runId: run.testSuiteRunID}).$promise;
            var resultPromised = $q.defer();
            comparisonResultsPromised.then(function (comparisonResultsResolved) {
                var result = false;
                for (var i = 0; i < comparisonResultsResolved.length; i++) {
                    var comparisonResult = comparisonResultsResolved[i];
                    var partialResult = needsToBeUpdatedOneComparisonResult(comparisonResult);
                    if (partialResult) {
                        result = true;
                        run.errorContent = (typeof run.errorContent !== 'undefined' && run.errorContent instanceof Array) ? run.errorContent : [];
                        var errorData = {};
                        errorData.name = comparisonResult.testName;
                        errorData.patternDate = comparisonResult.patternModificationDate;
                        errorData.sampleDate = comparisonResult.sampleModificationDate;
                        run.errorContent.push(errorData);
                    }
                }
                resultPromised.resolve(result);
            });
            return resultPromised.promise;
        };

        var getPromisedSuite = function () {
            var toBeResolvedSuite = ParticularSuite.query({testSuiteID: $route.current.params.testSuiteID}).$promise;
            toBeResolvedSuite.then(function (successValue) {
                $log.info(successValue);
                var promisedRuns = successValue.runs;
                for (var i = 0; i < promisedRuns.length; i++) {
                    var currentRun = promisedRuns[i];
                    updatePercentageOneRun(currentRun);
                    /*var currentNumberOfTests = getSumOfTests(currentRun);
                    var previousNumberOfTests = getSumOfTests(promisedRuns[i - 1]);
                    if (currentNumberOfTests > previousNumberOfTests && (i > 0)) {
                        currentRun.extraTests = currentNumberOfTests - previousNumberOfTests;
                    }*/
                    $log.info(currentRun);
                    currentRun.needsToBeUpdated = updateNeedsToBeUpdatedOneRun(currentRun);
                }

            });
            return toBeResolvedSuite;
        };

        return {
            getSuite: function () {
                return getPromisedSuite();
            }
        }
    }]);

visualTestingServices.factory('ResolveComparisonResults', ['$route', '$log', '$q', 'ParticularRun', function ($route, $log, $q, ParticularRun) {
        var getImageWidth = function (comparisonResult) {
            var defferedWidth = $q.defer();
            $("<img/>").attr("src", comparisonResult.sampleUrl).load(function () {
                defferedWidth.resolve(this.width);
            });
            return defferedWidth.promise;
        };

        var isDiff = function (result) {
            if (result.diffUrl !== null) {
                return true;
            }
            return false;
        };

        var setImageWidth = function (comparisonResult) {
            getImageWidth(comparisonResult).then(function (width) {
                comparisonResult.imageWidth = width;
            });
        };

        var getPromisedComparisonResults = function () {
            var promisedResults = ParticularRun.query({runId: $route.current.params.runId}).$promise;
            promisedResults.then(function (comparisonResults) {
                for (var i = 0; i < comparisonResults.length; i++) {
                    var comparisonResult = comparisonResults[i];
                    comparisonResult.isDiff = isDiff(comparisonResult);
                    setImageWidth(comparisonResult);
                }
                $log.info(promisedResults);
            });
            return promisedResults;

        };

        return {
            getComparisonResults: function () {
                return getPromisedComparisonResults();
            }
        }

    }]);