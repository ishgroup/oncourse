import nock from 'nock';
import config from 'config';
import ACTIONS from 'js/constants';
import { addCourse, removeCourse } from 'js/actions/cart';

let getDispatch = function(handleActions) {
    return function (action) {
        expect(action.type in handleActions).to.be.true;
        handleActions[action.type](action);
    };
};

describe('cart actions', () => {
    let handleActions;

    describe('ADD_TO_CART action', () => {
        let course = {
            id: 1,
            name: 'Course 1'
        };

        beforeEach(() => {
            handleActions = {
                [ACTIONS.ADD_TO_CART](action) {
                    expect(action, ACTIONS.ADD_TO_CART).to.deep.equal({
                        type: ACTIONS.ADD_TO_CART,
                        courseId: course.id
                    });
                }
            };
        });

        it('should create ADD_TO_CART and ADD_TO_CART_SUCCESS actions', (done) => {
            handleActions = {
                ...handleActions,
                [ACTIONS.ADD_TO_CART_SUCCESS](action) {
                    expect(action, ACTIONS.ADD_TO_CART_SUCCESS).to.deep.equal({
                        type: ACTIONS.ADD_TO_CART_SUCCESS,
                        course
                    });
                    done();
                }
            };

            nock(config.api_root)
                .put('/cart/courses/' + course.id)
                .reply(200, course);

            addCourse(course.id)(getDispatch(handleActions));
        });

        it('should create ADD_TO_CART and ADD_TO_CART_FAILURE actions', (done) => {
            handleActions = {
                ...handleActions,
                [ACTIONS.ADD_TO_CART_FAILURE](action) {
                    expect(action, ACTIONS.ADD_TO_CART_FAILURE).to.deep.equal({
                        type: ACTIONS.ADD_TO_CART_FAILURE,
                        courseId: course.id,
                        error: {}
                    });
                    done();
                }
            };

            nock(config.api_root)
                .put('/cart/courses/' + course.id)
                .replyWithError(500);

            addCourse(course.id)(getDispatch(handleActions));
        });
    });

    describe('REMOVE_FROM_CART action', () => {
        let courseId = 1;

        beforeEach(() => {
            handleActions = {
                [ACTIONS.REMOVE_FROM_CART](action) {
                    expect(action, ACTIONS.REMOVE_FROM_CART).to.deep.equal({
                        type: ACTIONS.REMOVE_FROM_CART,
                        courseId
                    });
                }
            };
        });


        it('should create REMOVE_FROM_CART and REMOVE_FROM_CART_SUCCESS actions', (done) => {
            handleActions = {
                ...handleActions,
                [ACTIONS.REMOVE_FROM_CART_SUCCESS](action) {
                    expect(action, ACTIONS.REMOVE_FROM_CART_SUCCESS).to.deep.equal({
                        type: ACTIONS.REMOVE_FROM_CART_SUCCESS,
                        courseId
                    });
                    done();
                }
            };

            nock(config.api_root)
                .delete('/cart/courses/' + courseId)
                .reply(204);

            removeCourse(courseId)(getDispatch(handleActions));
        });

        it('should create REMOVE_FROM_CART and REMOVE_FROM_CART_FAILURE actions', (done) => {
            handleActions = {
                ...handleActions,
                [ACTIONS.REMOVE_FROM_CART_FAILURE](action) {
                    expect(action, ACTIONS.REMOVE_FROM_CART_FAILURE).to.deep.equal({
                        type: ACTIONS.REMOVE_FROM_CART_FAILURE,
                        courseId,
                        error: {}
                    });
                    done();
                }
            };

            nock(config.api_root)
                .delete('/cart/course/' + courseId)
                .replyWithError(500);

            removeCourse(courseId)(getDispatch(handleActions));
        });
    });
});
