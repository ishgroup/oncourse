import config from 'config';
import request from 'js/lib/request';

// emit request
function emitRequest(item) {
    let d = $.Deferred();

    setTimeout(() => {
        d.resolve(item);
    }, 1000);

    return d.promise();
}

/**
 * Cart api
 * @module js/api/cart
 */

/**
 * @param {Object} courseClass course
 * @description adds course to cart
 * @returns {jqXHR}
 */
export function addClass(courseClass) {
    return emitRequest(courseClass);
    return request({
        type: 'put',
        url: config.api_root + '/cart/courses/' + courseClass.id
    });
}


/**
 * @param {Number} courseId course id
 * @description remove course from cart
 * @returns {jqXHR}
 */
export function removeClass(courseId) {
    return emitRequest(courseId);
    return request({
        type: 'delete',
        url: config.api_root + '/cart/courses/' + courseId
    });
}


/**
 * @param {Object} product product
 * @description adds product to cart
 * @returns {jqXHR}
 */
export function addProduct(product) {
    return emitRequest(product);
    return request({
        type: 'put',
        url: config.api_root + '/cart/products/' + product.id
    });
}

/**
 * @param {Object} productId product id
 * @description remove product from cart
 * @returns {jqXHR}
 */
export function removeProduct(productId) {
    return emitRequest(productId);
    return request({
        type: 'delete',
        url: config.api_root + '/cart/products/' + productId
    });
}

export default {
    addClass, removeClass,
    addProduct, removeProduct
};