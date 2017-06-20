import {Contact} from "../../model/web/Contact";
import {FULFILLED} from "../../common/actions/ActionUtils";
export const Actions = {
  ADD_CLASS_TO_CART: "ADD_CLASS_TO_CART",
  REMOVE_CLASS_FROM_CART: "REMOVE_CLASS_FROM_CART",

  ADD_PRODUCT_TO_CART: "ADD_PRODUCT_TO_CART",
  REMOVE_PRODUCT_FROM_CART: "REMOVE_PRODUCT_FROM_CART",

  ADD_PROMOTION_TO_CART: "ADD_PROMOTION_TO_CART",
  REMOVE_PROMOTION_FROM_CART: "REMOVE_PROMOTION_FROM_CART",

  HIDE_POPUP: "HIDE_POPUP",
  UPDATE_POPUP: "UPDATE_POPUP",
  REQUEST_COURSE_CLASS: "REQUEST_COURSE_CLASS",
  REQUEST_PRODUCT: "REQUEST_PRODUCT",
  REQUEST_PROMOTION: "REQUEST_PROMOTION",
  REQUEST_CONTACT: "REQUEST_CONTACT",

  SYNC_CART: "SYNC_CART",
  /**
   * Put a html loaded course class to this redux store
   */
  PutClassToStore: "web/courses/class/add",
};


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

export const addContact = function (contact: Contact) {
  return {type: FULFILLED(Actions.REQUEST_CONTACT), payload: contact};
};
