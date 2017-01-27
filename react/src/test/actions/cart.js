import nock from 'nock';
import config from 'config';
import ACTIONS from 'js/constants';
import { addClass, addProduct, removeClass, removeProduct } from 'js/actions/cart';

let getDispatch = function(handleActions) {
    return function (action) {
        expect(action.type in handleActions).to.be.true;
        handleActions[action.type](action);
    };
};

describe('cart actions', () => {
    let handleActions;

    let product, course;

    course = product = {
        id: 1,
        name: 'Item 1'
    };

    it('ADD_CLASS_TO_CART_SUCCESS', (done) => {
        handleActions = {
            [ACTIONS.ADD_CLASS_TO_CART_SUCCESS](action) {
                expect(action, ACTIONS.ADD_CLASS_TO_CART_SUCCESS).to.deep.equal({
                    type: ACTIONS.ADD_CLASS_TO_CART_SUCCESS,
                    data: course
                });
                done();
            }
        };

        nock(config.api_root)
            .put('/cart/courses/' + course.id)
            .reply(200, course);

        addClass(course)(getDispatch(handleActions));
    });

    it('REMOVE_CLASS_FROM_CART_SUCCESS', (done) => {
        handleActions = {
            [ACTIONS.REMOVE_CLASS_FROM_CART_SUCCESS](action) {
                expect(action, ACTIONS.REMOVE_CLASS_FROM_CART_SUCCESS).to.deep.equal({
                    type: ACTIONS.REMOVE_CLASS_FROM_CART_SUCCESS,
                    id: course.id
                });
                done();
            }
        };

        nock(config.api_root)
            .delete('/cart/courses/' + course.id)
            .reply(204);

        removeClass(course.id)(getDispatch(handleActions));
    });

    it('ADD_PRODUCT_TO_CART_SUCCESS', (done) => {
        handleActions = {
            [ACTIONS.ADD_PRODUCT_TO_CART_SUCCESS](action) {
                expect(action, ACTIONS.ADD_PRODUCT_TO_CART_SUCCESS).to.deep.equal({
                    type: ACTIONS.ADD_PRODUCT_TO_CART_SUCCESS,
                    data: product
                });
                done();
            }
        };

        nock(config.api_root)
            .put('/cart/products/' + product.id)
            .reply(200, product);

        addProduct(product)(getDispatch(handleActions));
    });

    it('REMOVE_PRODUCT_FROM_CART_SUCCESS', (done) => {
        handleActions = {
            [ACTIONS.REMOVE_PRODUCT_FROM_CART_SUCCESS](action) {
                expect(action, ACTIONS.REMOVE_PRODUCT_FROM_CART_SUCCESS).to.deep.equal({
                    type: ACTIONS.REMOVE_PRODUCT_FROM_CART_SUCCESS,
                    id: product.id
                });
                done();
            }
        };

        nock(config.api_root)
            .delete('/cart/products/' + product.id)
            .reply(204);

        removeProduct(product.id)(getDispatch(handleActions));
    });
});
