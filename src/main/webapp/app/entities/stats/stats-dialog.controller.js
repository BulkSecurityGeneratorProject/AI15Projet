(function() {
    'use strict';

    angular
        .module('ai15App')
        .controller('StatsDialogController', StatsDialogController);

    StatsDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Stats'];

    function StatsDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Stats) {
        var vm = this;

        vm.stats = entity;
        vm.clear = clear;
        vm.save = save;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.stats.id !== null) {
                Stats.update(vm.stats, onSaveSuccess, onSaveError);
            } else {
                Stats.save(vm.stats, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('ai15App:statsUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
