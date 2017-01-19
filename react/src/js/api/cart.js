import config from 'config';
import request from 'js/lib/request';

/**
 * Cart api
 * @module js/api/cart
 */

/**
 * @param {Number} courseId course id
 * @description adds course to cart
 * @returns {jqXHR}
 */
export function addCourse(courseId) {
    return request({
        type: 'put',
        url: config.api_root + '/cart/courses/' + courseId
    });
}


/**
 * @param {Number} courseId course id
 * @description remove course from cart
 * @returns {jqXHR}
 */
export function removeCourse(courseId) {
    return request({
        type: 'delete',
        url: config.api_root + '/cart/courses/' + courseId
    });
}

export default {
    addCourse, removeCourse
};