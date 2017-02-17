import ACTIONS from '../constants';
import cartApi from '../api/cart';
import {CommonCartItem} from "../services/IshState";

/**
 * Add course class to cart
 * @param {Object} course Course class
 * @return {Function}
 */
export const addClass = addCartItem({
    add: cartApi.addClass,
    action: ACTIONS.ADD_CLASS_TO_CART_SUCCESS,
    fieldId: 'id',
    fieldItem: 'data'
});

/**
 * Remove course class from cart
 * @param {Number} courseId Course id
 * @return {Function}
 */
export const removeClass = removeCartItem({
    remove: cartApi.removeClass,
    action: ACTIONS.REMOVE_CLASS_FROM_CART_SUCCESS,
    fieldId: 'id'
});

/**
 * Add product to cart
 * @param {Object} product Product
 * @return {Function}
 */
export const addProduct = addCartItem({
    add: cartApi.addProduct,
    action: ACTIONS.ADD_PRODUCT_TO_CART_SUCCESS,
    fieldId: 'id',
    fieldItem: 'data'
});

/**
 * Remove product from cart
 * @param {Number} productId Product id
 * @return {Function}
 */
export const removeProduct = removeCartItem({
    remove: cartApi.removeProduct,
    action: ACTIONS.REMOVE_PRODUCT_FROM_CART_SUCCESS,
    fieldId: 'id'
});

/**
 * Add discount to cart
 * @param {Number} code Code of discount
 * @return {Function}
 */
export const addDiscount = addCartItem({
    add: cartApi.addDiscount,
    action: ACTIONS.ADD_DISCOUNT_TO_CART_SUCCESS,
    fieldId: 'code',
    fieldItem: 'data'
});

/**
 * Remove discount from cart
 * @param {Number} code Code of discount
 * @return {Function}
 */
export const removeDiscount = removeCartItem({
    remove: cartApi.removeDiscount,
    action: ACTIONS.REMOVE_DISCOUNT_FROM_CART_SUCCESS,
    fieldId: 'code'
});

function addCartItem(params) {
  return function (item: CommonCartItem) {
    return (dispatch) => {
      let req = params.add(item);

      return req.then((item) => {
        dispatch({
          type: params.action,
          [params.fieldItem]: item
        });
      });
    }
  };
}

function removeCartItem(params) {
  return function (id) {
    return (dispatch) => {
      let req = params.remove(id);

      return req.then(() => {
        dispatch({
          type: params.action,
          [params.fieldId]: id
        });
      });
    }
  };
}
