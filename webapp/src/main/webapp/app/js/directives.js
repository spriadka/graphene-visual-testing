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
        var run = scope.info;
        console.log(run.needsToBeUpdated);
        if (run.needsToBeUpdated) {
            var spanElem = "<span class=\"glyphicon glyphicon-ok-circle\"></span>";
            var message = "PATTERNS OUT OF DATE";
            $(elem).addClass("alert alert-danger").html(spanElem + message).show();
            $compile(elem.contents())(scope);
        }
        else {
            var spanElem = "<span class=\"glyphicon glyphicon-ok-circle\"></span>";
            var message = "ALL PATTERNS UP TO DATE";
            $(elem).addClass("alert alert-success").html(spanElem + message).show();
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

visualTestingDirectives.directive('runInfo',function ($compile) {
    var linker = function(scope,elem,attr){
        console.log(scope);
        var isDiff = scope.result.isDiff;
        var urlOfTemplate;
        if (isDiff){
            urlOfTemplate = 'app/partials/directives/runInfoDiff.html';
        }
        else {
            urlOfTemplate = 'app/partials/directives/runInfoSuccessful.html';
        }
        var promisedTemplate = $.get(urlOfTemplate);
        promisedTemplate.then(function (html){
            $(elem).html(html).show();
            $compile(elem.contents())(scope);
        });
        
    };
    return {
        restrict: 'A',
        scope: {
            result: '='
        },
        link: linker
        

    };
}
);

visualTestingDirectives.directive('jcrop', ['$injector', function () {

        return {
            restrict: 'A',
            link: function (scope, elem, attr) {
                $(elem).Jcrop({
                    bgColor: 'black',
                    multi: false
                });

            }
        };
    }]);



