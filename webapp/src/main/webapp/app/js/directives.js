/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


var visualTestingDirectives = angular.module('visualTestingDirectives',[]);

visualTestingDirectives.directive('runInfo',function(){
    return {
        restrict: 'E',
        scope: {
            run: '='
        },
        templateUrl: function(scope){
            if (angular.isDefined(scope.diffUrl)){
                return 'app/partials/directives/runInfoDiff.html';
            }
            else {
                return 'app/partials/directives/runInfoSuccessful.html';
            }
        } 
    };
});