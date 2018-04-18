(function() {
    'use strict';

    angular
        .module('proxyServerApp')
        .controller('ProxyDetailController', ProxyDetailController);

    ProxyDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Proxy'];

    function ProxyDetailController($scope, $rootScope, $stateParams, previousState, entity, Proxy) {
        var vm = this;

        vm.proxy = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('proxyServerApp:proxyUpdate', function(event, result) {
            vm.proxy = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
