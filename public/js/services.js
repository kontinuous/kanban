angular.module('kanbanServices', ['ngResource']).
    factory('Task', function($resource){
      return $resource('/boards/:boardId/tasks/:taskId', {}, {});
    }).
    factory('Board', function($resource){
      return $resource('/boards/:boardId', {}, {});
    }).
    factory('User', function($resource){
      return $resource('/users/:userId', {}, {});
    }).
    config(['$routeProvider', function($routeProvider) {
      $routeProvider.
          when('/boards', {templateUrl: 'partials/boards-list.html',   controller: BoardCtrl}).
          when('/boards/:boardId', {templateUrl: 'partials/board-details.html', controller: TaskCtrl}).
          when('/boards/:boardId/edit', {templateUrl: 'partials/board-edit.html', controller: TaskCtrl}).
          when('/profile', {templateUrl: 'partials/user-details.html', controller: ProfileCtrl }).
          otherwise({redirectTo: '/boards'});
    }]).
    directive ('unfocus', function() { return {
        restrict: 'A',
        link: function (scope, element, attribs) {

            element[0].focus();

            element.bind ("blur", function() {
                scope.$apply(attribs["unfocus"]);
            });
        }
    } });
