import ACTIONS from 'js/constants';
import cart from 'js/reducers/cart';

describe('cart reducer', () => {

    it('should return initial state', () => {
        expect(cart(undefined, {})).to.deep.equal({
            courses: []
        });
    });

    describe('ADD_TO_CART action handling', () => {

        let course1, course2;

        beforeEach(() => {
            course1 = {
                id: 1,
                name: 'Course 1'
            };
            course2 = {
                id: 2,
                name: 'Course 2'
            };
        });

        it('should handle ADD_TO_CART', () => {
            let state = cart({
                courses: [{
                    id: course1.id,
                    pending: true
                }, {
                    id: course2.id,
                    pending: false,
                    error: {}
                }]
            }, {
                type: ACTIONS.ADD_TO_CART,
                courseId: 1
            });

            expect(state, 'Add pending course').to.deep.equal({
                courses: [{
                    id: course1.id,
                    pending: true
                }, {
                    id: course2.id,
                    pending: false,
                    error: {}
                }]
            });

            state = cart(state, {
                type: ACTIONS.ADD_TO_CART,
                courseId: 2
            });

            expect(state, 'Add course with error').to.deep.equal({
                courses: [{
                    id: course1.id,
                    pending: true
                }, {
                    id: course2.id,
                    pending: true
                }]
            });

            state = cart(state, {
                type: ACTIONS.ADD_TO_CART,
                courseId: 3
            });

            expect(state, 'Add new course').to.deep.equal({
                courses: [{
                    id: course1.id,
                    pending: true
                }, {
                    id: course2.id,
                    pending: true
                }, {
                    id: 3,
                    pending: true
                }]
            });
        });


        it('should handle ADD_TO_CART_SUCCESS', () => {
            let state = cart({
                courses: [{
                    id: course1.id,
                    pending: true
                }, {
                    id: course2.id,
                    pending: true
                }]
            }, {
                type: ACTIONS.ADD_TO_CART_SUCCESS,
                course: course2
            });

            expect(state).to.deep.equal({
                courses: [{
                    id: course1.id,
                    pending: true
                }, {
                    id: course2.id,
                    pending: false,
                    data: course2
                }]
            });

            state = cart(state, {
                type: ACTIONS.ADD_TO_CART_SUCCESS,
                course: course1
            });

            expect(state).to.deep.equal({
                courses: [{
                    id: course1.id,
                    pending: false,
                    data: course1
                }, {
                    id: course2.id,
                    pending: false,
                    data: course2
                }]
            });

            state = cart(state, {
                type: ACTIONS.ADD_TO_CART_SUCCESS,
                course: course1
            });

            expect(state, 'Add added course').to.deep.equal({
                courses: [{
                    id: course1.id,
                    pending: false,
                    data: course1
                }, {
                    id: course2.id,
                    pending: false,
                    data: course2
                }]
            });
        });

        it('should handle ADD_TO_CART_FAILURE', () => {
            let state = cart({
                courses: [{
                    id: course1.id,
                    pending: true
                }, {
                    id: course2.id,
                    pending: true
                }]
            }, {
                type: ACTIONS.ADD_TO_CART_FAILURE,
                courseId: course2.id,
                error: {}
            });

            expect(state).to.deep.equal({
                courses: [{
                    id: course1.id,
                    pending: true
                }, {
                    id: course2.id,
                    pending: false,
                    error: {}
                }]
            });
        });
    });

    describe('REMOVE_FROM_CART action handling', () => {
        let course1, course2;

        beforeEach(() => {
            course1 = {
                id: 1,
                name: 'Course 1'
            };
            course2 = {
                id: 2,
                name: 'Course 2'
            };
        });

        it('should handle REMOVE_FROM_CART', () => {
            let state = cart({
                courses: [{
                    id: course1.id,
                    data: course1,
                    pending: false
                }]
            }, {
                type: ACTIONS.REMOVE_FROM_CART,
                courseId: 1
            });

            expect(state, 'Remove existed course').to.deep.equal({
                courses: [{
                    id: course1.id,
                    data: course1,
                    pending: true
                }]
            });

            state = cart({
                courses: [{
                    id: course1.id,
                    data: course1,
                    pending: true
                }]
            }, {
                type: ACTIONS.REMOVE_FROM_CART,
                courseId: 1
            });

            expect(state, 'Remove pending course').to.deep.equal({
                courses: [{
                    id: course1.id,
                    data: course1,
                    pending: true
                }]
            });

            state = cart({
                courses: [{
                    id: course1.id,
                    data: course1,
                    pending: false,
                    error: {}
                }]
            }, {
                type: ACTIONS.REMOVE_FROM_CART,
                courseId: 1
            });

            expect(state, 'Remove course with error').to.deep.equal({
                courses: [{
                    id: course1.id,
                    data: course1,
                    pending: true
                }]
            });
        });

        it('should handle REMOVE_FROM_CART_SUCCESS', () => {
            let state = cart({
                courses: [{
                    id: course1.id,
                    data: course1,
                    pending: true
                }, {
                    id: course2.id,
                    data: course2,
                    pending: false
                }]
            }, {
                type: ACTIONS.REMOVE_FROM_CART_SUCCESS,
                courseId: course1.id
            });

            expect(state).to.deep.equal({
                courses: [{
                    id: course2.id,
                    data: course2,
                    pending: false
                }]
            });
        });

        it('should handle REMOVE_FROM_CART_FAILURE', () => {
            let state = cart({
                courses: [{
                    id: course1.id,
                    data: course1,
                    pending: true
                }, {
                    id: course2.id,
                    data: course2,
                    pending: false
                }]
            }, {
                type: ACTIONS.REMOVE_FROM_CART_FAILURE,
                courseId: course1.id,
                error: {}
            });

            expect(state).to.deep.equal({
                courses: [{
                    id: course1.id,
                    data: course1,
                    pending: false,
                    error: {}
                }, {
                    id: course2.id,
                    data: course2,
                    pending: false
                }]
            });
        });
    });
});
