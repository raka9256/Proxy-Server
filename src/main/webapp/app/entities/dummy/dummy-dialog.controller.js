(function() {
    'use strict';

    angular
        .module('proxyServerApp')
        .controller('DummyDialogController', DummyDialogController);

    DummyDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Dummy'];

    function DummyDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Dummy) {
        var vm = this;

        vm.dummy = entity;
        vm.clear = clear;
        vm.save = save;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.dummy.id !== null) {
                Dummy.update(vm.dummy, onSaveSuccess, onSaveError);
            } else {
                Dummy.save(vm.dummy, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('proxyServerApp:dummyUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
