(function() {
    'use strict';
    angular
        .module('proxyServerApp')
        .factory('Proxy', Proxy);

    Proxy.$inject = ['$resource', 'DateUtils'];

    function Proxy ($resource, DateUtils) {
        var resourceUrl =  'api/proxies/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.dateModified = DateUtils.convertLocalDateFromServer(data.dateModified);
                    }
                    return data;
                }
            },
            'update': {
                method: 'PUT',
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    copy.dateModified = DateUtils.convertLocalDateToServer(copy.dateModified);
                    return angular.toJson(copy);
                }
            },
            'save': {
                method: 'POST',
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    copy.dateModified = DateUtils.convertLocalDateToServer(copy.dateModified);
                    return angular.toJson(copy);
                }
            }
        });
    }
})();
