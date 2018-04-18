(function() {
    'use strict';

    angular
        .module('proxyServerApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('dummy', {
            parent: 'entity',
            url: '/dummy?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Dummies'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/dummy/dummies.html',
                    controller: 'DummyController',
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
            }
        })
        .state('dummy-detail', {
            parent: 'dummy',
            url: '/dummy/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Dummy'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/dummy/dummy-detail.html',
                    controller: 'DummyDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'Dummy', function($stateParams, Dummy) {
                    return Dummy.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'dummy',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('dummy-detail.edit', {
            parent: 'dummy-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/dummy/dummy-dialog.html',
                    controller: 'DummyDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Dummy', function(Dummy) {
                            return Dummy.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('dummy.new', {
            parent: 'dummy',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/dummy/dummy-dialog.html',
                    controller: 'DummyDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                username: null,
                                email: null,
                                website: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('dummy', null, { reload: 'dummy' });
                }, function() {
                    $state.go('dummy');
                });
            }]
        })
        .state('dummy.edit', {
            parent: 'dummy',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/dummy/dummy-dialog.html',
                    controller: 'DummyDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Dummy', function(Dummy) {
                            return Dummy.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('dummy', null, { reload: 'dummy' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('dummy.delete', {
            parent: 'dummy',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/dummy/dummy-delete-dialog.html',
                    controller: 'DummyDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Dummy', function(Dummy) {
                            return Dummy.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('dummy', null, { reload: 'dummy' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
