(function() {
    'use strict';

    angular
        .module('ai15App')
        .controller('HomeController', HomeController);

    HomeController.$inject = ['$scope','$window', 'Principal', 'LoginService', '$state', 'Book', 'BookSearch'];

    function HomeController ($scope, $window, Principal, LoginService, $state, Book, BookSearch) {
        var vm = this;
        
        /*vm.books = Book.query();
        vm.search = BookSearch.query();
        */
        vm.coucou = coucou;
        vm.account = null;
        vm.isAuthenticated = null;
        vm.login = LoginService.open;
        vm.register = register;
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
        function register () {
            $state.go('register');
        }
        
        function coucou(){
        	$window.alert("coucou");
        }
        
        vm.books = [];
        vm.clear = clear;
        vm.search = search;
        vm.loadAll = loadAll;

        loadAll();

        function loadAll() {
            Book.query(function(result) {
                vm.books = result;
                vm.searchQuery = null;
            });
        }

        function search() {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            BookSearch.query({query: vm.searchQuery}, function(result) {
                vm.books = result;
                vm.currentSearch = vm.searchQuery;
            });
        }

        function clear() {
            vm.searchQuery = null;
            loadAll();
        } 
        
    }
      
})();
