var myapp = angular.module('kanban', ['ui','kanbanServices']);

function ProfileCtrl($scope, User, $http) {
  $http.get('/users/profile').success(function(data) {
    $scope.user = data;
  });
  $http.get('/users/shared').success(function(data) {
    $scope.shared_boards = data;
  });
  $http.get('/boards').success(function(data) {
    $scope.my_boards = data;
  });
}

function BoardEditorCtrl($scope, Board) {
    $scope.enableEdit = function() {
        $scope.edit = true;
    };

    $scope.disableEdit = function() {
        $scope.board.$save({boardId: $scope.board.id}, function() {
            $scope.edit = false;
        });
    };
}

function BoardCtrl($scope, Board) {

  $scope.boards = Board.query();

  $scope.saveBoard = function() {
    var newBoard = new Board({name:$scope.boardName});
    newBoard.$save(function() {
      $scope.boards = Board.query();
    });
    $scope.boardName = '';
  };
}

function BoardEditCtrl($scope, Board, $routeParams, $location, User, $http) {

  $scope.boardId = $routeParams.boardId;

  $scope.board = Board.get({boardId: $scope.boardId}, function() {
    $scope.users = User.query(function(data, getResponseHeaders) {
      angular.forEach($scope.board.sharedUsers, function(userInBoard) {
        for(var i=0; i<data.length; i++) {
          if(data[i].name === userInBoard.name) {
            data.splice(i, 1);
            break;
          }
        }
      });
    });
  });

  //$scope.users = User.query();

  $scope.editorEnabled = false;

  $scope.saveBoard = function() {
    $scope.board.name = $scope.form_board_name;
    $scope.board.$save({boardId: $scope.boardId}, function() {
      $scope.disableEditor();
    });
  };

  $scope.enableEditor = function() {
    $scope.editorEnabled = true;
    $scope.form_board_name = $scope.board.name;
  };

  $scope.disableEditor = function() {
    $scope.editorEnabled = false;
  };

  $scope.syncModel = function (e, ui) {
    //$scope.board.$save({boardId: $scope.boardId});
    if(ui.sender) {
      $scope.board.$save({boardId: $scope.boardId});
    }
  }
}

function TaskCtrl($scope, Task, $routeParams) {
  $scope.boardId = $routeParams.boardId;

  $scope.updateModel = function() {
    return Task.query({boardId:$scope.boardId}, function(tasks, getResponseHeaders) {
      $scope.groupped = {};
      angular.forEach($scope.statuses, function(status) {
        $scope.groupped[status.name] = []
      });

      angular.forEach(tasks, function(task) {
        $scope.groupped[task.status].push(task);
      });
    });
  };

  $scope.groupped = {};

  $scope.tasks = $scope.updateModel();

  $scope.statuses = [
    {id: 1, name: "pending"},
    {id: 2, name: "inProgress"},
    {id: 3, name: "done"}
  ];

  $scope.addTodo = function() {
    var newTask = new Task({name:$scope.todoText});
    newTask.$save({boardId:$scope.boardId}, function() {
      $scope.tasks = $scope.updateModel();
    });
    $scope.todoText = '';
  };

  $scope.syncModel = function (e, ui) {
    for(var status in $scope.groupped) {
      angular.forEach($scope.groupped[status], function(task) {
        if(task.status != status) {
          console.log(task);
          task.status = status;
          task.$save({boardId:$scope.boardId, taskId:task.id});
        }
      });
    }
  }
}