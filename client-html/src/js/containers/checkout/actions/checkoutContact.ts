/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Contact } from "@api/model";
import { _toRequestType, FULFILLED } from "../../../common/actions/ActionUtils";

export const CHECKOUT_GET_CONTACT = _toRequestType("checkout/get/contact");
export const CHECKOUT_GET_CONTACT_FULFILLED = FULFILLED(CHECKOUT_GET_CONTACT);
export const CHECKOUT_CLEAR_CONTACT_EDIT_RECORD = "checkout/clear/contact/edit/record";

export const CHECKOUT_UPDATE_CONTACT = _toRequestType("checkout/update/contact");
export const CHECKOUT_UPDATE_CONTACT_FULFILLED = FULFILLED(CHECKOUT_UPDATE_CONTACT);

export const CHECKOUT_CREATE_CONTACT = _toRequestType("checkout/post/contact");

export const CHECKOUT_UPDATE_CONTACT_RELATIONS = _toRequestType("checkout/update/contact/relations");

export const CHECKOUT_GET_RELATED_CONTACT = _toRequestType("checkout/get/related/contact");
export const CHECKOUT_GET_RELATED_CONTACT_FULFILLED = FULFILLED(CHECKOUT_GET_RELATED_CONTACT);

export const checkoutGetContact = (id: number) => ({
  type: CHECKOUT_GET_CONTACT,
  payload: id
});

export const checkoutClearContactEditRecord = () => ({
  type: CHECKOUT_CLEAR_CONTACT_EDIT_RECORD
});

export const checkoutUpdateContact = (id: string, contact: Contact) => ({
  type: CHECKOUT_UPDATE_CONTACT,
  payload: { id, contact }
});

export const checkoutCreateContact = (contact: Contact) => ({
  type: CHECKOUT_CREATE_CONTACT,
  payload: contact
});

export const getRelatedContacts = (search: string, columns?: string, ascending?: boolean, sort?: string) => ({
  type: CHECKOUT_GET_RELATED_CONTACT,
  payload: {
 search, columns, ascending, sort 
}
});
