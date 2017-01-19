(function() {
    'use strict';

    angular
        .module('ai15App')
        .factory('BibliothequeSearch', BibliothequeSearch);

    BibliothequeSearch.$inject = ['$resource'];

    function BibliothequeSearch($resource) {
        var resourceUrl =  'api/_search/bibliotheques/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
