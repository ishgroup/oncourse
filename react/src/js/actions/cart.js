import ACTIONS from '../constants';
import cartApi from 'js/api/cart';
import { getError } from 'js/lib/utils';

export function addCourse(courseId) {
    return (dispatch) => {
        let req = cartApi.addCourse(courseId);

        dispatch({
            type: ACTIONS.ADD_TO_CART,
            courseId
        });

        req.then((course) => {
            dispatch({
                type: ACTIONS.ADD_TO_CART_SUCCESS,
                course
            });
        }, () => {
            dispatch({
                type: ACTIONS.ADD_TO_CART_FAILURE,
                courseId: courseId,
                error: getError()
            });
        });
    };
}

export function removeCourse(courseId) {
    return (dispatch) => {
        let req = cartApi.removeCourse(courseId);

        dispatch({
            type: ACTIONS.REMOVE_FROM_CART,
            courseId
        });

        req.then(() => {
            dispatch({
                type: ACTIONS.REMOVE_FROM_CART_SUCCESS,
                courseId
            });
        }, () => {
            dispatch({
                type: ACTIONS.REMOVE_FROM_CART_FAILURE,
                courseId: courseId,
                error: getError()
            });
        });
    };
}