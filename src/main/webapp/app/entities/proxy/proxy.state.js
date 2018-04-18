(function() {
    'use strict';

    angular
        .module('proxyServerApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('proxy', {
            parent: 'entity',
            url: '/proxy?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Proxies'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/proxy/proxies.html',
                    controller: 'ProxyController',
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
        .state('proxy-detail', {
            parent: 'proxy',
            url: '/proxy/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Proxy'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/proxy/proxy-detail.html',
                    controller: 'ProxyDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'Proxy', function($stateParams, Proxy) {
                    return Proxy.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'proxy',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('proxy-detail.edit', {
            parent: 'proxy-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/proxy/proxy-dialog.html',
                    controller: 'ProxyDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Proxy', function(Proxy) {
                            return Proxy.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('proxy.new', {
            parent: 'proxy',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/proxy/proxy-dialog.html',
                    controller: 'ProxyDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                api: null,
                                contentType: null,
                                header: null,
                                dateModified: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('proxy', null, { reload: 'proxy' });
                }, function() {
                    $state.go('proxy');
                });
            }]
        })
        .state('proxy.edit', {
            parent: 'proxy',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/proxy/proxy-dialog.html',
                    controller: 'ProxyDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Proxy', function(Proxy) {
                            return Proxy.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('proxy', null, { reload: 'proxy' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('proxy.delete', {
            parent: 'proxy',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/proxy/proxy-delete-dialog.html',
                    controller: 'ProxyDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Proxy', function(Proxy) {
                            return Proxy.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('proxy', null, { reload: 'proxy' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
