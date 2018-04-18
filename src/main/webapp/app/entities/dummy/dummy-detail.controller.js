(function() {
    'use strict';

    angular
        .module('proxyServerApp')
        .controller('DummyDetailController', DummyDetailController);

    DummyDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Dummy'];

    function DummyDetailController($scope, $rootScope, $stateParams, previousState, entity, Dummy) {
        var vm = this;

        vm.dummy = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('proxyServerApp:dummyUpdate', function(event, result) {
            vm.dummy = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
