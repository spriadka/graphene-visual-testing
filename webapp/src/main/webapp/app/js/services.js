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
        return {
            all: $resource('rest/runs/comparison-result/:runId', {runId: '@runId'}, {
                query: {method: 'GET', isArray: true}}),
            filter: $resource('rest/runs/comparison-result/filter/:runId', {runId: '@runId', testClass: name, diffsOnly: '@diffsOnly'}, {
                query: {method: 'GET', isArray: true}})

        };
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

visualTestingServices.factory('NodeService', ['$resource', function ($resource) {
        return $resource('rest/nodes/:nodeId', {nodeId: '@nodeId'}, {query: {method: 'GET', isArray: false}});
    }]);

visualTestingServices.factory('ResolveSuite', ['$route', 'ParticularSuite', 'ParticularRun', '$q', '$log', function ($route, ParticularSuite, ParticularRun, $q, $log) {

        var updateNeedsToBeUpdatedOneRun = function (run) {
            var comparisonResultsPromised = ParticularRun.all.query({runId: run.testSuiteRunID}).$promise;
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
            var toBeResolvedSuite = ParticularSuite.query({testSuiteID: $route.current.params.testSuiteID, fetch: "runs,rootNode"}).$promise;
            toBeResolvedSuite.then(function (successValue) {
                $log.info(successValue);
                var promisedRuns = successValue.runs;
                /*for (var i = 0; i < promisedRuns.length; i++) {
                    var currentRun = promisedRuns[i];
                    var currentNumberOfTests = currentRun.numberOfTests;
                    var previousNumberOfTests = promisedRuns[i - 1].numberOfTests;
                    if (currentNumberOfTests > previousNumberOfTests && (i > 0)) {
                        currentRun.extraTests = currentNumberOfTests - previousNumberOfTests;
                    }
                    /*if (currentRun.needsToBeUpdated) {
                        
                    }
                    $log.info(currentRun);
                }*/

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
        var getPromisedComparisonResults = function () {
            var promisedResults;
            var testClass = $route.current.params.testClass;
            var diffsOnly = $route.current.params.diffsOnly;
            promisedResults = (testClass && diffsOnly)
                    ? ParticularRun.filter.query({runId: $route.current.params.runId, testClass: testClass, diffsOnly: diffsOnly}).$promise
                    : ParticularRun.all.query({runId: $route.current.params.runId}).$promise;
            promisedResults.then(function (comparisonResults) {
                for (var i = 0; i < comparisonResults.length; i++) {
                    comparisonResults[i].imageWidth = getImageWidth(comparisonResults[i]);
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

visualTestingServices.factory('Mask',function(){
    var Mask = function(){
        this.maskID = null;
        this.sourceData = null;
        this.sourceUrl = null;
        this.horizontalAlignment = null;
        this.verticalAlignment = null;
        this.top = 0;
        this.left = 0;
        this.width = 0;
        this.height = 0;
        this.pattern = null;
        this.testSuiteName = null;
    };
    Mask.fromJson = function(obj){
        if (typeof obj == "string") obj = JSON.parse(obj);
        var instance = new Mask();
        instance.setMaskID(obj.maskID);
        instance.setHeight(obj.height);
        instance.setLeft(obj.left);
        instance.setTop(obj.top);
        instance.setWidth(obj.width);
        instance.sourceUrl = obj.sourceUrl;
        instance.testSuiteName = obj.testSuiteName;
        return instance;
    };
    Mask.prototype = {
        constructor: Mask,
        getSourceData: function(){
            return this.sourceData;
        },
        setSourceData: function(sourceData){
            this.sourceData = sourceData;
        },
        getHorizontalAlignment: function(){
            return this.horizontalAlignment;
        },
        setHorizontalAlignment: function(horizontalAlignment){
            this.horizontalAlignment = horizontalAlignment;
        },
        getVerticalAlignment: function(){
            return this.verticalAlignment;
        },
        setVerticalAlignment: function(verticalAlignment){
            this.verticalAlignment = verticalAlignment;
        },
        getTop: function(){
            return this.top;
        },
        setTop: function(top){
            this.top = top;
        },
        getLeft: function(){
            return this.left;
        },
        setLeft: function(left){
            this.left = left;
        },
        getWidth: function(){
            return this.width;
        },
        setWidth: function(width){
            this.width = width;
        },
        getHeight: function(){
            return this.height;
        },
        setHeight: function(height){
            this.height = height;
        },
        setMaskID: function(maskID){
            this.maskID = maskID;
        }
    };
    return Mask;
});

visualTestingServices.factory('CropService',function(){
    
});