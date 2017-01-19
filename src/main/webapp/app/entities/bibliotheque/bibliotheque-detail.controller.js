(function() {
    'use strict';

    angular
        .module('ai15App')
        .controller('BibliothequeDetailController', BibliothequeDetailController);

    BibliothequeDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Bibliotheque', 'User', 'Book'];

    function BibliothequeDetailController($scope, $rootScope, $stateParams, previousState, entity, Bibliotheque, User, Book) {
        var vm = this;

        vm.bibliotheque = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('ai15App:bibliothequeUpdate', function(event, result) {
            vm.bibliotheque = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
