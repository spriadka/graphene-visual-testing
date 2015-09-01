/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/* global angular */

var visualTestingDirectives = angular.module('visualTestingDirectives', []);


visualTestingDirectives.directive('alertInfo', function ($compile) {
    console.log("Directive was called");
    var linker = function (scope, elem, attr) {
        console.log(scope.info.needsToBeUpdated);
        console.log(scope.info.successfulPercentage);
        console.log(scope.info.failedPercentage);
        console.log(scope.info.failedTestPercentage);
        if (!scope.info.needsToBeUpdated) {
            console.log("SCOPE INFO");
            console.log(scope);
            console.log(scope.info.successfulPercentage);
            var spanElem = "<span class=\"glyphicon glyphicon-ok-circle\"></span>";
            var message = "ALL PATTERNS UP TO DATE";
            $(elem).addClass("alert alert-success").html(spanElem + message).show();
            $compile(elem.contents())(scope);
        }
        else {
            console.log("SCOPE INFO");
            console.log(scope);
            console.log(scope.info.successfulPercentage);
            var spanElem = "<span class=\"glyphicon glyphicon-ok-circle\"></span>";
            var message = "ALL PATTERNS UP TO DATE";
            $(elem).addClass("alert alert-danger").html(spanElem + message).show();
            $compile(elem.contents())(scope);
        }
    };
    return {
        restrict: 'A',
        scope: {
            info: '='
        },
        link: linker
    };
});

/*visualTestingDirectives.directive('runInfo', function () {
 return {
 restrict: 'E',
 scope: {
 run: '='
 },
 templateUrl: function (scope) {
 if (scope.diffUrl) {
 return 'app/partials/directives/runInfoDiff.html';
 }
 else {
 
 return 'app/partials/directives/runInfoSuccessful.html';
 
 }
 }
 
 };
 }
 );
 */
visualTestingDirectives.directive('jcrop', ['$injector', function () {
        return {
            restrict: 'A',
            link: function (scope, elem, attr) {
                $(elem).Jcrop({
                    bgColor: 'black',
                    multi: true
                },function(){
                    var crop = this;
                    console.log(crop);
                });
                
            }
        };
    }]);



