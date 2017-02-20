import {ConfigConstants} from '../config/ConfigConstants';
import request from '../lib/request';
import $ from 'jquery';

// emit request
function emitRequest(item) {
    let d = $.Deferred();

    setTimeout(() => {
        d.resolve(item);
    }, 0);

    return d.promise();
}

/**
 * @param {Object} courseClass course
 * @description adds course to cart
 * @returns {jqXHR}
 */
export function addClass(courseClass) {
    return emitRequest(courseClass);
    // return request({
    //     type: 'put',
    //     url: ConfigConstants.API_ROOT + '/cart/courses/' + courseClass.id
    // });
}


/**
 * @param {Number} courseId course id
 * @description remove course from cart
 * @returns {jqXHR}
 */
export function removeClass(courseId) {
    return emitRequest(courseId);
    // return request({
    //     type: 'delete',
    //     url: ConfigConstants.API_ROOT + '/cart/courses/' + courseId
    // });
}


/**
 * @param {Object} product product
 * @description adds product to cart
 * @returns {jqXHR}
 */
export function addProduct(product) {
    return emitRequest(product);
    // return request({
    //     type: 'put',
    //     url: ConfigConstants.API_ROOT + '/cart/products/' + product.id
    // });
}

/**
 * @param {Object} productId product id
 * @description remove product from cart
 * @returns {jqXHR}
 */
export function removeProduct(productId) {
    return emitRequest(productId);
    // return request({
    //     type: 'delete',
    //     url: ConfigConstants.API_ROOT + '/cart/products/' + productId
    // });
}

/**
 * @param {Object} code Promo code
 * @description adds discount to cart
 * @returns {jqXHR}
 */
export function addDiscount(code) {
    return request({
        type: 'put',
        url: ConfigConstants.API_ROOT + '/cart/discounts/' + code
    });
}

/**
 * @param {Object} code Promo code
 * @description remove discount from cart
 * @returns {jqXHR}
 */
export function removeDiscount(code) {
    return request({
        type: 'delete',
        url: ConfigConstants.API_ROOT + '/cart/discounts/' + code
    });
}

export default {
    addClass, removeClass,
    addProduct, removeProduct,
    addDiscount, removeDiscount
};
