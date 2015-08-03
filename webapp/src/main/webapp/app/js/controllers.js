'use strict';

/* Controllers */

var visualTestingControllers = angular.module('visualTestingControllers', []);

visualTestingControllers.controller('SuiteListCtrl', ['$scope', '$route', '$log',
    'Suites', 'ParticularSuite', 'DeleteParticularSuite',
    function ($scope, $route, $log, Suites, ParticularSuite, DeleteParticularSuite) {
        $scope.suites = Suites.query();
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
        $scope.isRun = function (run){
            if (run === null || angular.isUndefined(run)){
                return false;
            }
            var success = run.numberOfSuccessfullComparisons;
            var failed = run.numberOfFailedComparisons;
            var failedTests = run.numberOfFailedFunctionalTests;
            var sum = success + failed + failedTests;
            if (sum === 0){
                return false;
            }
            return true;
        };
        $scope.getSuccessfullPercentage = function (run) {
            var success = run.numberOfSuccessfullComparisons;
            var failed = run.numberOfFailedComparisons;
            var failedTests = run.numberOfFailedFunctionalTests;
            var sum = success + failed + failedTests;
            return 100 * (success / sum);
        };
        $scope.getFailedPercentage = function (run){
            var success = run.numberOfSuccessfullComparisons;
            var failed = run.numberOfFailedComparisons;
            var failedTests = run.numberOfFailedFunctionalTests;
            var sum = success + failed + failedTests;
            return 100 * (failed / sum);
        };
        $scope.getFailedTestsPercentage = function (run){
            var success = run.numberOfSuccessfullComparisons;
            var failed = run.numberOfFailedComparisons;
            var failedTests = run.numberOfFailedFunctionalTests;
            var sum = success + failed + failedTests;
            return 100 * (failedTests / sum);
        };
    }]);

visualTestingControllers.controller('ParticularSuiteCtrl', ['$scope', '$routeParams',
    '$route', '$log', 'ParticularSuite', 'DeleteParticularSuiteRun',
    function ($scope, $routeParams, $route, $log, ParticularSuite, DeleteParticularSuiteRun) {
        $scope.particularSuite = ParticularSuite.query({testSuiteID: $routeParams.testSuiteID});
        $scope.timestampToDate = timestampToDate;
        $scope.deleteSuiteRun = function (testSuiteRunID) {
            var promise =
                    DeleteParticularSuiteRun.deleteParticularSuiteRun(testSuiteRunID);
            promise.then(
                    function (payload) {
                        $route.reload();
                        $log.info('suite run deleted sucessfully', payload);
                    },
                    function (errorPayload) {
                        $log.error('failure delete suite run', errorPayload);
                    });
        };
    }]);

visualTestingControllers.controller('ParticularRunCtrl', ['$scope', '$routeParams', '$log',
    '$route', '$location', 'ParticularRun', 'RejectSample', 'RejectPattern',
    function ($scope, $routeParams, $log, $route, $location, ParticularRun,
            RejectSample, RejectPattern) {
        $scope.comparisonResults = ParticularRun.query({runId: $routeParams.runId});
        $scope.back = back;
        $scope.rejectPattern = function (diffID) {
            var promise =
                    RejectPattern.rejectPattern(diffID);
            promise.then(
                    function (payload) {
                        if ($scope.comparisonResults.length == 1) {
                            back();
                        } else {
                            $route.reload();
                        }
                        $log.info('suite deleted sucessfully', payload);
                    },
                    function (errorPayload) {
                        $log.error('failure delete suite', errorPayload);
                    });
        };
        $scope.rejectSample = function (diffID) {
            var promise =
                    RejectSample.rejectSample(diffID);
            promise.then(
                    function (payload) {
                        if ($scope.comparisonResults.length == 1) {
                            back();
                        } else {
                            $route.reload();
                        }
                        $log.info('suite deleted sucessfully', payload);
                    },
                    function (errorPayload) {
                        $log.error('failure delete suite', errorPayload);
                    });
        }
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
}

var back = function () {
    window.history.back();
}
