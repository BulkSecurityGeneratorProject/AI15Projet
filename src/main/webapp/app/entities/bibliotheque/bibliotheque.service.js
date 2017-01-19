(function() {
    'use strict';
    angular
        .module('ai15App')
        .factory('Bibliotheque', Bibliotheque);

    Bibliotheque.$inject = ['$resource'];

    function Bibliotheque ($resource) {
        var resourceUrl =  'api/bibliotheques/:id';

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
