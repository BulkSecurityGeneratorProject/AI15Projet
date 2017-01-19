(function() {
    'use strict';

    angular
        .module('ai15App')
        .controller('BibliothequeDeleteController',BibliothequeDeleteController);

    BibliothequeDeleteController.$inject = ['$uibModalInstance', 'entity', 'Bibliotheque'];

    function BibliothequeDeleteController($uibModalInstance, entity, Bibliotheque) {
        var vm = this;

        vm.bibliotheque = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Bibliotheque.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
