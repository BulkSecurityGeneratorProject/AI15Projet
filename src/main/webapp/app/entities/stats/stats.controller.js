(function() {
    'use strict';

    angular
        .module('ai15App')
        .controller('StatsController', StatsController);

    StatsController.$inject = ['$scope', '$state', 'Stats', 'StatsSearch'];

    function StatsController ($scope, $state, Stats, StatsSearch) {
        var vm = this;

        vm.stats = [];
        vm.clear = clear;
        vm.search = search;
        vm.loadAll = loadAll;

        loadAll();

        function loadAll() {
            Stats.query(function(result) {
                vm.stats = result;
                vm.searchQuery = null;
            });
        }

        function search() {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            StatsSearch.query({query: vm.searchQuery}, function(result) {
                vm.stats = result;
                vm.currentSearch = vm.searchQuery;
            });
        }

        function clear() {
            vm.searchQuery = null;
            loadAll();
        }    }
})();
