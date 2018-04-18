(function() {
    'use strict';
    angular
        .module('proxyServerApp')
        .factory('Dummy', Dummy);

    Dummy.$inject = ['$resource'];

    function Dummy ($resource) {
        var resourceUrl =  'api/dummies/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' },
            'proxy': {
                method: 'GET',
                url: 'api/proxiesEnc/:enc',
                isArray: true,
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        }
                        return data;
                    }
            }
        });
    }
})();
