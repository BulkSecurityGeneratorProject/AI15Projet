(function() {
    'use strict';
    angular
        .module('ai15App')
        .factory('Stats', Stats);

    Stats.$inject = ['$resource'];

    function Stats ($resource) {
        var resourceUrl =  'api/stats/:id';

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
            'update': { method:'PUT' }
        });
    }
})();
