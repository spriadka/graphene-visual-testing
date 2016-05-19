'use strict';

var PARTIALS = 'app/partials';

/* App Module */

var visualTestingApp = angular.module('visualTestingApp', [
    'ngRoute',
    'visualTestingControllers',
    'visualTestingDirectives',
    'visualTestingServices',
    'ui.bootstrap'
]);

visualTestingApp.config(['$routeProvider',
    function ($routeProvider) {
        $routeProvider.
                when('/suites', {
                    templateUrl: PARTIALS + '/test-suite-list.html',
                    controller: 'SuiteListCtrl'
                }).
                when('/suites/:testSuiteID', {
                    templateUrl: PARTIALS + '/test-suite-runs-list.html',
                    controller: 'ParticularSuiteCtrl',
                    resolve: {
                        promisedSuite: function (ResolveSuite) {
                            return ResolveSuite.getSuite();
                        }
                    }
                }).
                when('/suites/:testSuiteID/runs/:runId', {
                    templateUrl: PARTIALS + '/particular-run.html',
                    controller: 'ParticularRunCtrl',
                    resolve: {
                        runs: function (ResolveComparisonResults) {
                            return ResolveComparisonResults.getComparisonResults();
                        }
                    }
                }).
                otherwise({
                    redirectTo: '/suites'
                });
    }]);