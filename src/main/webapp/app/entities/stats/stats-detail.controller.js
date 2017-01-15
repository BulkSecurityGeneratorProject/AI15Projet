(function() {
    'use strict';

    angular
        .module('ai15App')
        .controller('StatsDetailController', StatsDetailController);

    StatsDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Stats'];

    function StatsDetailController($scope, $rootScope, $stateParams, previousState, entity, Stats) {
        var vm = this;

        vm.stats = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('ai15App:statsUpdate', function(event, result) {
            vm.stats = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
