(function() {
    'use strict';

    angular
        .module('ai15App')
        .controller('BibliothequeDialogController', BibliothequeDialogController);

    BibliothequeDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Bibliotheque', 'User', 'Book', 'Principal', 'LoginService'];

    function BibliothequeDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Bibliotheque, User, Book, Principal, LoginService) {
        var vm = this;

        vm.bibliotheque = entity;
        vm.clear = clear;
        vm.save = save;
        vm.users = User.query();
        vm.books = Book.query();
        
        vm.account = null;
        vm.isAuthenticated = null;
        vm.login = LoginService.open;
        $scope.$on('authenticationSuccess', function() {
            getAccount();
        });

        getAccount();

        function getAccount() {
            Principal.identity().then(function(account) {
                vm.account = account;
                vm.isAuthenticated = Principal.isAuthenticated;
            });
        }
            

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.bibliotheque.id !== null) {
                Bibliotheque.update(vm.bibliotheque, onSaveSuccess, onSaveError);
            } else {
                Bibliotheque.save(vm.bibliotheque, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('ai15App:bibliothequeUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
