(function() {
    'use strict';

    angular
        .module('ai15App')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('bibliotheque', {
            parent: 'entity',
            url: '/bibliotheque?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'ai15App.bibliotheque.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/bibliotheque/bibliotheques.html',
                    controller: 'BibliothequeController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'id,asc',
                    squash: true
                },
                search: null
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        search: $stateParams.search
                    };
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('bibliotheque');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('bibliotheque-detail', {
            parent: 'entity',
            url: '/bibliotheque/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'ai15App.bibliotheque.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/bibliotheque/bibliotheque-detail.html',
                    controller: 'BibliothequeDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('bibliotheque');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Bibliotheque', function($stateParams, Bibliotheque) {
                    return Bibliotheque.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'bibliotheque',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('bibliotheque-detail.edit', {
            parent: 'bibliotheque-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/bibliotheque/bibliotheque-dialog.html',
                    controller: 'BibliothequeDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Bibliotheque', function(Bibliotheque) {
                            return Bibliotheque.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('bibliotheque.new', {
            parent: 'bibliotheque',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/bibliotheque/bibliotheque-dialog.html',
                    controller: 'BibliothequeDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('bibliotheque', null, { reload: 'bibliotheque' });
                }, function() {
                    $state.go('bibliotheque');
                });
            }]
        })
        .state('bibliotheque.edit', {
            parent: 'bibliotheque',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/bibliotheque/bibliotheque-dialog.html',
                    controller: 'BibliothequeDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Bibliotheque', function(Bibliotheque) {
                            return Bibliotheque.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('bibliotheque', null, { reload: 'bibliotheque' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('bibliotheque.delete', {
            parent: 'bibliotheque',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/bibliotheque/bibliotheque-delete-dialog.html',
                    controller: 'BibliothequeDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Bibliotheque', function(Bibliotheque) {
                            return Bibliotheque.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('bibliotheque', null, { reload: 'bibliotheque' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
