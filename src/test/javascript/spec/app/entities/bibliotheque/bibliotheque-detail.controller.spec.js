'use strict';

describe('Controller Tests', function() {

    describe('Bibliotheque Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockPreviousState, MockBibliotheque, MockUser, MockBook;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockPreviousState = jasmine.createSpy('MockPreviousState');
            MockBibliotheque = jasmine.createSpy('MockBibliotheque');
            MockUser = jasmine.createSpy('MockUser');
            MockBook = jasmine.createSpy('MockBook');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity,
                'previousState': MockPreviousState,
                'Bibliotheque': MockBibliotheque,
                'User': MockUser,
                'Book': MockBook
            };
            createController = function() {
                $injector.get('$controller')("BibliothequeDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'ai15App:bibliothequeUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
