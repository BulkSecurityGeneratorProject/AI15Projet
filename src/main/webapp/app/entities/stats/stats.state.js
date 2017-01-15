(function() {
    'use strict';

    angular
        .module('ai15App')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('stats', {
            parent: 'entity',
            url: '/stats',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'ai15App.stats.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/stats/stats.html',
                    controller: 'StatsController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('stats');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('stats-detail', {
            parent: 'entity',
            url: '/stats/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'ai15App.stats.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/stats/stats-detail.html',
                    controller: 'StatsDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('stats');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Stats', function($stateParams, Stats) {
                    return Stats.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'stats',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('stats-detail.edit', {
            parent: 'stats-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/stats/stats-dialog.html',
                    controller: 'StatsDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Stats', function(Stats) {
                            return Stats.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('stats.new', {
            parent: 'stats',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/stats/stats-dialog.html',
                    controller: 'StatsDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                titre: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('stats', null, { reload: 'stats' });
                }, function() {
                    $state.go('stats');
                });
            }]
        })
        .state('stats.edit', {
            parent: 'stats',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/stats/stats-dialog.html',
                    controller: 'StatsDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Stats', function(Stats) {
                            return Stats.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('stats', null, { reload: 'stats' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('stats.delete', {
            parent: 'stats',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/stats/stats-delete-dialog.html',
                    controller: 'StatsDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Stats', function(Stats) {
                            return Stats.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('stats', null, { reload: 'stats' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
