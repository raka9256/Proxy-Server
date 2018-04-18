(function() {
    'use strict';

    angular
        .module('proxyServerApp')
        .controller('ProxyDeleteController',ProxyDeleteController);

    ProxyDeleteController.$inject = ['$uibModalInstance', 'entity', 'Proxy'];

    function ProxyDeleteController($uibModalInstance, entity, Proxy) {
        var vm = this;

        vm.proxy = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Proxy.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
