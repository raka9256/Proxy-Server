(function() {
    'use strict';

    angular
        .module('proxyServerApp')
        .controller('DummyDeleteController',DummyDeleteController);

    DummyDeleteController.$inject = ['$uibModalInstance', 'entity', 'Dummy'];

    function DummyDeleteController($uibModalInstance, entity, Dummy) {
        var vm = this;

        vm.dummy = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Dummy.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
