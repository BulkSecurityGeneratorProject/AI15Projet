(function() {
    'use strict';

    angular
        .module('ai15App')
        .controller('StatsDeleteController',StatsDeleteController);

    StatsDeleteController.$inject = ['$uibModalInstance', 'entity', 'Stats'];

    function StatsDeleteController($uibModalInstance, entity, Stats) {
        var vm = this;

        vm.stats = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Stats.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
