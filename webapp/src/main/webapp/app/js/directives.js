/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/* global angular */

var visualTestingDirectives = angular.module('visualTestingDirectives', []);


visualTestingDirectives.directive('alertInfo', function ($compile) {

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

    var createMessage = function (run) {
        console.log(run.errorContent);
        var message = "<div>";
        for (var i = 0; i < run.errorContent.length; i++) {
            var errorPattern = run.errorContent[i];
            var fromDate = timestampToDate(parseInt(errorPattern.patternDate));
            message += "Pattern for test: " + "<b>" + errorPattern.name + "</b>" + " was modified " + "<b>" + fromDate + "</b>";
            message += "<br/>";
        }
        if (run.extraTests) {
            message += "Added <b>" + run.extraTests + "</b> extra tests";
        }
        message += "</div>";
        return message;
    };

    var linker = function (scope, elem, attr) {
        var run = scope.info;
        if (run.needsToBeUpdated) {
            var spanElem = "<span class=\"glyphicon glyphicon-exclamation-sign\"></span>";
            var message = "TESTS WERE MODIFIED";
            var numOutDatedPatterns = run.errorContent.length;
            var extraTests = run.extraTests ? 1 : 0;
            numOutDatedPatterns += extraTests;
            console.log("ERROR CONTENT: " + numOutDatedPatterns);
            var badgeNotification = "<span class=\"badge\" style=\"margin-right: 20px; margin-left: 5px;\" >" + numOutDatedPatterns + "</span>";
            $(elem).addClass("alert alert-danger").html(spanElem + badgeNotification + message).show();
            $(elem).attr("data-toggle", "popover").attr("data-container", "body").attr("data-html", "true").attr("data-placement", "bottom").attr("data-content", createMessage(run));
            $(elem).popover();
            $compile(elem.contents())(scope);
        }
        else {
            var spanElem = "<span class=\"glyphicon glyphicon-ok-circle\" style=\"margin-right: 20px;\"></span>";
            var message = "NO TESTS WERE CHANGED";
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

visualTestingDirectives.directive('runInfo', function ($compile) {
    var linker = function (scope, elem, attr) {
        var isDiff = scope.result.isDiff;
        var urlOfTemplate;
        if (isDiff) {
            urlOfTemplate = 'app/partials/directives/runInfoDiff.html';
        }
        else {
            urlOfTemplate = 'app/partials/directives/runInfoSuccessful.html';
        }
        var promisedTemplate = $.get(urlOfTemplate);
        promisedTemplate.then(function (html) {
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

visualTestingDirectives.directive('nodeNav', ['$compile', '$timeout', function ($compile, $timeout, $element) {
        return {
            restrict: 'A',
            scope: {
                'parent': '='
            },
            controller: function ($scope, $element) {
                $scope.selected = $scope.parent.children[0].nodeId;
                console.log($scope);
                $scope.index = $scope.parent.children[0].index;
                $scope.$emit('select-change', $scope.selected);
                $scope.$on('collapse-others', function (event, data) {
                    if ($scope.parent && $scope.index > data) {
                        console.log("DESTROYING");
                        $timeout(function () {
                            $scope.$destroy();
                            $($element).remove();
                        });
                    }
                });
                $scope.$on('collapse', function (event, nodeIdToCollapse) {
                    if ($scope.parent.nodeId === nodeIdToCollapse) {
                        $scope.$emit('select-change', $scope.parent.nodeId);
                        $scope.$emit('selections-splice', $scope.index - 1);
                        $timeout(function () {
                            $scope.$destroy();
                            $($element).remove();
                        });
                    }
                });
                $scope.collapseOthers = function (index) {
                    console.log($scope);
                    $scope.$parent.$broadcast('collapse-others', index);
                    $scope.$emit('select-change', $scope.selected);
                    $scope.$emit('selections-splice', $scope.index);
                };
            }
            ,
            template: '<select class="form-control" ng-model="selected" ng-change="collapseOthers(index)" ng-options="node.nodeId as node.word.value for node in parent.children">' +
                    /*'<option ng-repeat="node in parent.children" ' + 
                     'value="{{node.nodeId}}"' +  
                     'label="{{node.word.value}}" ' +  
                     '></option>' +*/
                    '</select>'
        };
    }]);

visualTestingDirectives.directive('jcrop', function () {


    return {
        restrict: 'C',
        scope: true,
        link: function (scope, elem, attr) {
            console.log("directive called");
            var comparisonResult = scope.$parent.result;
            console.log(comparisonResult);
            $(elem).attr("src", comparisonResult.sampleUrl);
            $(elem).attr("patternid", comparisonResult.patternID);
            $(elem).Jcrop({
                bgColor: 'black',
                multi: true
            }, function () {
                comparisonResult.jcrop_api = this;
                var container = comparisonResult.jcrop_api.container;
                container.on('cropcreate', function (element, selection, coordinates) {
                    var shadesColor = comparisonResult.jcrop_api.opt.bgColor;
                    var shades = $(element.currentTarget).children(".jcrop-shades").children("div");
                    if ($(shades).css("background-color") !== shadesColor) {
                        $(shades).css("background-color", shadesColor);
                    }
                });
                if (comparisonResult.masks.length !== 0) {
                    for (var i = 0; i < comparisonResult.masks.length; i++) {
                        var mask = comparisonResult.masks[i];
                        var selection = comparisonResult.jcrop_api.newSelection();
                        selection.update($.Jcrop.wrapFromXywh([mask.left, mask.top, mask.width, mask.height]));
                        selection.maskID = mask.maskID;
                        selection.setColor("#00ffd4", 0.3);
                    }
                }
            });

        }
    };
});



