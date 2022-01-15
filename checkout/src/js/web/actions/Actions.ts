import { FULFILLED } from '../../common/actions/ActionUtils';
import { ContactState } from '../../services/IshState';
import { CourseClass, Product } from '../../model';
import { GABuilder } from '../../services/GoogleAnalyticsService';

export const Actions = {
  ADD_CLASS_TO_CART: 'ADD_CLASS_TO_CART',
  REMOVE_CLASS_FROM_CART: 'REMOVE_CLASS_FROM_CART',

  ADD_WAITING_COURSE_TO_CART: 'ADD_WAITING_COURSE_TO_CART',
  REMOVE_WAITING_COURSE_FROM_CART: 'REMOVE_WAITING_COURSE_FROM_CART',

  ADD_PRODUCT_TO_CART: 'ADD_PRODUCT_TO_CART',
  REMOVE_PRODUCT_FROM_CART: 'REMOVE_PRODUCT_FROM_CART',

  ADD_PROMOTION_TO_CART: 'ADD_PROMOTION_TO_CART',
  REMOVE_PROMOTION_FROM_CART: 'REMOVE_PROMOTION_FROM_CART',

  HIDE_POPUP: 'HIDE_POPUP',
  UPDATE_POPUP: 'UPDATE_POPUP',
  REQUEST_COURSE_CLASS: 'REQUEST_COURSE_CLASS',
  REQUEST_INACTIVE_COURSE: 'REQUEST_INACTIVE_COURSE',
  REQUEST_WAITING_COURSE: 'REQUEST_WAITING_COURSE',
  REQUEST_PRODUCT: 'REQUEST_PRODUCT',
  REQUEST_PROMOTION: 'REQUEST_PROMOTION',
  REQUEST_CONTACT: 'REQUEST_CONTACT',
  REQUEST_SUGGESTION: 'REQUEST_SUGGESTION',

  SYNC_CART: 'SYNC_CART',
  /**
   * Put a html loaded course class to this redux store
   */
  PutClassToStore: 'web/courses/class/add',
};

/**
 * Request course class by id
 */
export function requestCourseClass(id: string, addToCart?: boolean) {
  return {
    type: Actions.REQUEST_COURSE_CLASS,
    payload: { id, addToCart }
  };
}

export const requestSuggestion = () => ({
  type: Actions.REQUEST_SUGGESTION
});

/**
 * Request product by id
 */
export function requestProduct(id: string, addToCart?: boolean) {
  return {
    type: Actions.REQUEST_PRODUCT,
    payload: { id, addToCart }
  };
}

/**
 * add product to cart
 */
export function addProductToCart(product: Product) {
  return {
    type: Actions.ADD_PRODUCT_TO_CART,
    payload: product,
    meta: {
      analytics: GABuilder.addProductToCart(product),
    },
  };
}

/**
 * add course class to cart
 */
export function addClassToCart(courseClass: CourseClass) {
  return {
    type: Actions.ADD_CLASS_TO_CART,
    payload: courseClass,
    meta: {
      analytics: GABuilder.addCourseClassToCart('class', courseClass),
    },
  };
}

/**
 * Update popup by passed content
 * @param {VirtualDOM} Content content which you want to display in popup
 * @return {Object} Action UPDATE_POPUP
 */
export function updatePopup(content) {
  return {
    content,
    type: Actions.UPDATE_POPUP,
  };
}

/**
 * Hide popup
 * @return {Object} Action HIDE_POPUP
 */
export function hidePopup() {
  return {
    type: Actions.HIDE_POPUP,
  };
}

export const addContact = function (contact: ContactState) {
  return { type: FULFILLED(Actions.REQUEST_CONTACT), payload: contact };
};
