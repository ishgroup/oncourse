import ACTIONS from 'js/constants';
import cart from 'js/reducers/cart';

describe('cart reducer', () => {
    let course1, course2, product1, product2, state, initialState;

    beforeEach(() => {
        initialState = {
            courses: [],
            products: [],
            discounts: []
        };
        product1 = course1 = {
            id: 1,
            name: 'Item 1'
        };
        product2 = course2 = {
            id: 2,
            name: 'Course 2'
        };
    });

    it('should return initial state', () => {
        expect(cart(undefined, {})).to.deep.equal(initialState);
    });

    describe('ADD_CLASS_TO_CART_SUCCESS', () => {
        it('should add course class to cart', () => {
            state = cart(undefined, {
                type: ACTIONS.ADD_CLASS_TO_CART_SUCCESS,
                data: course1
            });

            state = cart(state, {
                type: ACTIONS.ADD_CLASS_TO_CART_SUCCESS,
                data: course2
            });

            expect(state).to.be.deep.equal(Object.assign(initialState, { courses: [course1, course2] }));
        });

        it('should return previous state because of course class already is in cart', () => {
            state = cart(undefined, {
                type: ACTIONS.ADD_CLASS_TO_CART_SUCCESS,
                data: course1
            });

            state = cart(state, {
                type: ACTIONS.ADD_CLASS_TO_CART_SUCCESS,
                data: course1
            });

            expect(state).to.be.deep.equal(Object.assign(initialState, { courses: [course1] }));
        });
    });

    describe('REMOVE_CLASS_FROM_CART_SUCCESS', () => {
        it('should remove course class from cart', () => {
            state = cart(undefined, {
                type: ACTIONS.ADD_CLASS_TO_CART_SUCCESS,
                data: course1
            });

            state = cart(state, {
                type: ACTIONS.ADD_CLASS_TO_CART_SUCCESS,
                data: course2
            });

            state = cart(state, {
                type: ACTIONS.REMOVE_CLASS_FROM_CART_SUCCESS,
                id: course1.id
            });

            expect(state).to.be.deep.equal(Object.assign(initialState, { courses: [course2] }));
        });

        it('should return previous state because of course class isn\'t in cart', () => {
            state = cart(undefined, {
                type: ACTIONS.ADD_CLASS_TO_CART_SUCCESS,
                data: course1
            });

            state = cart(state, {
                type: ACTIONS.REMOVE_CLASS_FROM_CART_SUCCESS,
                id: course2.id
            });

            expect(state).to.be.deep.equal(Object.assign(initialState, { courses: [course1] }));
        });
    });

    describe('ADD_PRODUCT_TO_CART_SUCCESS', () => {
        it('should add product to cart', () => {
            state = cart(undefined, {
                type: ACTIONS.ADD_PRODUCT_TO_CART_SUCCESS,
                data: product1
            });

            state = cart(state, {
                type: ACTIONS.ADD_PRODUCT_TO_CART_SUCCESS,
                data: product2
            });

            expect(state).to.be.deep.equal(Object.assign(initialState, { products: [product1, product2] }));
        });

        it('should return previous state because of product already is in cart', () => {
            state = cart(undefined, {
                type: ACTIONS.ADD_PRODUCT_TO_CART_SUCCESS,
                data: product1
            });

            state = cart(state, {
                type: ACTIONS.ADD_PRODUCT_TO_CART_SUCCESS,
                data: product1
            });

            expect(state).to.be.deep.equal(Object.assign(initialState, { products: [product1] }));
        });
    });

    describe('REMOVE_PRODUCT_FROM_CART_SUCCESS', () => {
        it('should remove product from cart', () => {
            state = cart(undefined, {
                type: ACTIONS.ADD_PRODUCT_TO_CART_SUCCESS,
                data: product1
            });

            state = cart(state, {
                type: ACTIONS.ADD_PRODUCT_TO_CART_SUCCESS,
                data: product2
            });

            state = cart(state, {
                type: ACTIONS.REMOVE_PRODUCT_FROM_CART_SUCCESS,
                id: product1.id
            });

            expect(state).to.be.deep.equal(Object.assign(initialState, { products: [product2] }));
        });

        it('should return previous state because of product isn\'t in cart', () => {
            state = cart(undefined, {
                type: ACTIONS.ADD_PRODUCT_TO_CART_SUCCESS,
                data: product1
            });

            state = cart(state, {
                type: ACTIONS.REMOVE_PRODUCT_FROM_CART_SUCCESS,
                id: product2.id
            });

            expect(state).to.be.deep.equal(Object.assign(initialState, { products: [product1] }));
        });
    });
});
