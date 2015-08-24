'use strict';

var PARTIALS = 'app/partials';

/* App Module */

var visualTestingApp = angular.module('visualTestingApp', [
  'ngRoute',
  'visualTestingControllers',
  'visualTestingServices',
  'ui.bootstrap'
]);

visualTestingApp.config(['$routeProvider',
  function($routeProvider) {
    $routeProvider.
      when('/suites', {
        templateUrl: PARTIALS + '/test-suite-list.html',
        controller: 'SuiteListCtrl'
      }).
      when('/suites/:testSuiteID', {
        templateUrl: PARTIALS + '/test-suite-runs-list.html',
        controller: 'ParticularSuiteCtrl'
      }).
      when('/suites/:testSuiteID/runs/:runId', {
        templateUrl: PARTIALS + '/particular-run.html',
        controller: 'ParticularRunCtrl'
      }).
      otherwise({
        redirectTo: '/suites'
      });
  }]);