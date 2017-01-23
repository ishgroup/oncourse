import ACTIONS from '../constants';
import cartApi from 'js/api/cart';
import { getError } from 'js/lib/utils';

/**
 * Cart actions
 * @module js/actions/cart
 */

/**
 * Add course to cart
 * @param {Number} courseId Course id
 * @return {Function}
 */
export function addClass(courseId) {
    return (dispatch) => {
        let req = cartApi.addClass(courseId);

        dispatch({
            type: ACTIONS.ADD_CLASS_TO_CART,
            courseId
        });

        return req.then((course) => {
            dispatch({
                type: ACTIONS.ADD_CLASS_TO_CART_SUCCESS,
                course
            });
        }, () => {
            dispatch({
                type: ACTIONS.ADD_CLASS_TO_CART_FAILURE,
                courseId: courseId,
                error: getError()
            });
        });
    };
}


/**
 * Remove course from cart
 * @param {Number} courseId Course id
 * @return {Function}
 */
export function removeClass(courseId) {
    return (dispatch) => {
        let req = cartApi.removeClass(courseId);

        dispatch({
            type: ACTIONS.REMOVE_CLASS_FROM_CART,
            courseId
        });

        return req.then(() => {
            dispatch({
                type: ACTIONS.REMOVE_CLASS_FROM_CART_SUCCESS,
                courseId
            });
        }, () => {
            dispatch({
                type: ACTIONS.REMOVE_CLASS_FROM_CART_FAILURE,
                courseId: courseId,
                error: getError()
            });
        });
    };
}