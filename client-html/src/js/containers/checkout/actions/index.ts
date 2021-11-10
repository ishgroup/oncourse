/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import {
  CheckoutEnrolmentCustom,
  CheckoutProductPurchase,
  CheckoutSaleRelationExtended
} from "../../../model/checkout";

export const CHECKOUT_ADD_CONTACT = "checkout/add/contact";
export const CHECKOUT_REMOVE_CONTACT = "checkout/remove/contact";
export const CHECKOUT_UPDATE_CONTACT = "checkout/update/contact";

export const CHECKOUT_ADD_ITEM = "checkout/add/item";
export const CHECKOUT_ADD_ITEMS = "checkout/add/items";
export const CHECKOUT_UPDATE_CLASS_ITEM = "checkout/update/classItem";
export const CHECKOUT_REMOVE_ITEM = "checkout/remove/item";

export const CHECKOUT_CHANGE_STEP = "checkout/change/step";
export const CHECKOUT_TOGGLE_SUMMARY_ITEM = "checkout/toggle/summary/item";
export const CHECKOUT_TOGGLE_SUMMARY_VOUCHER_ITEM = "checkout/toggle/summary/voucherItem";
export const CHECKOUT_SUMMARY_SEND_CONTEXT = "checkout/summary/send/context";

export const CHECKOUT_SET_HAS_ERRORS = "checkout/set/hasErrors";
export const CHECKOUT_CLEAR_STATE = "checkout/clear/state";

export const CHECKOUT_UPDATE_RELATED_ITEMS = "checkout/update/relatedItems";

export const checkoutUpdateRelatedItems = (cartItems: CheckoutSaleRelationExtended[], suggestItems: CheckoutSaleRelationExtended[]) => ({
  type: CHECKOUT_UPDATE_RELATED_ITEMS,
  payload: { cartItems, suggestItems }
});

export const checkoutClearState = () => ({
  type: CHECKOUT_CLEAR_STATE
});

export const addContact = (contact, isPayer?: boolean, checkItems?: boolean) => ({
  type: CHECKOUT_ADD_CONTACT,
  payload: { contact, isPayer, checkItems }
});

export const removeContact = (contactIndex: number) => ({
  type: CHECKOUT_REMOVE_CONTACT,
  payload: { contactIndex }
});

export const updateContact = (contact, id) => ({
  type: CHECKOUT_UPDATE_CONTACT,
  payload: { contact, id }
});

export const addItem = item => ({
  type: CHECKOUT_ADD_ITEM,
  payload: { item }
});

export const updateClassItem = item => ({
  type: CHECKOUT_UPDATE_CLASS_ITEM,
  payload: { item }
});

export const removeItem = (itemId: number, itemType: string) => ({
  type: CHECKOUT_REMOVE_ITEM,
  payload: { itemId, itemType }
});

export const changeStep = (step: number) => ({
  type: CHECKOUT_CHANGE_STEP,
  payload: { step }
});

export const toggleSummaryItem = (listIndex: number, itemIndex: number) => ({
  type: CHECKOUT_TOGGLE_SUMMARY_ITEM,
  payload: { listIndex, itemIndex }
});

export const toggleVoucherItem = (itemIndex: number) => ({
  type: CHECKOUT_TOGGLE_SUMMARY_VOUCHER_ITEM,
  payload: { itemIndex }
});

export const toggleSendContext = (listIndex: number, type: string) => ({
  type: CHECKOUT_SUMMARY_SEND_CONTEXT,
  payload: { listIndex, type }
});

export const checkoutAddItems = (items: { enrolments?: CheckoutEnrolmentCustom[], purchases?: CheckoutProductPurchase[] }) => ({
  type: CHECKOUT_ADD_ITEMS,
  payload: items
});

export const checkoutSetHasErrors = hasErrors => ({
  type: CHECKOUT_SET_HAS_ERRORS,
  payload: hasErrors
});

