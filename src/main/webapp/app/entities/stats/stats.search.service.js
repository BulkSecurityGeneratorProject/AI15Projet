(function() {
    'use strict';

    angular
        .module('ai15App')
        .factory('StatsSearch', StatsSearch);

    StatsSearch.$inject = ['$resource'];

    function StatsSearch($resource) {
        var resourceUrl =  'api/_search/stats/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
