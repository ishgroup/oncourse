import {_toRequestType} from "../../../../common/actions/ActionUtils";
import {ContactNode, Contact, PurchaseItem, CorporatePass} from "../../../../model";

import {ContactNodeToState, ItemToState, State} from "../reducers/State";
import {IAction} from "../../../../actions/IshAction";
import {ContactProps} from "../components/Index";

export const ADD_CONTACT_TO_SUMMARY: string = _toRequestType("checkout/summary/add/contact/to/summary");

export const PROCEED_TO_PAYMENT: string = "checkout/summary/proceed/to/payment";

export const UPDATE_ENROLMENT_FIELDS: string = "checkout/summary/update/enrolment/fields";

export const ItemsLoad: string = "checkout/summary/items/load";

export const GET_CONTACT_NODE_AND_MODEL_FROM_BACKEND: string = "checkout/summary/get/ContactNode/from/backend";
export const GET_ALL_CONTACT_NODES_FROM_BACKEND: string = "checkout/summary/get/ContactNodes";

export const ADD_CONTACT_NODE_TO_STATE: string = "checkout/summary/update/ContactNode";
export const REWRITE_CONTACT_NODE_TO_STATE: string = "checkout/summary/rewrite/ContactNode";
export const REWRITE_CONTACT_NODES_TO_STATE: string = "checkout/summary/rewrite/ContactNodes";

export const SELECT_ITEM_REQUEST: string = "checkout/summary/select/item/request";

export const UPDATE_ITEM: string = "checkout/summary/update/item";
export const REPLACE_ITEM: string = "checkout/summary/replace/item";

export const REMOVE_ITEM_FROM_SUMMARY: string = "checkout/remove/summary/item";
export const REMOVE_CONTACT_FROM_SUMMARY: string = "checkout/remove/summury/contact";

export const SET_RESULT_DETAILS_CONTACTS = "checkout/result/set/details/contacts";
export const SET_RESULT_DETAILS_CORPORATE_PASS = "checkout/result/set/details/corporatePass";

export const setResultDetailsCorporatePass = (corporatePass: CorporatePass) => ({
  type: SET_RESULT_DETAILS_CORPORATE_PASS,
  payload: corporatePass
})

export const setResultDetailsContacts = (contacts: ContactProps[]) => ({
  type: SET_RESULT_DETAILS_CONTACTS,
  payload: contacts
})

export const addContactToSummary = (contact: Contact, uncheckItems = false): IAction<{contact: Contact, uncheckItems?: boolean}> => {
  return {
    type: ADD_CONTACT_TO_SUMMARY,
    payload: {contact, uncheckItems}
  };
};


/**
 * an user has selected/unselected this item and we need to update all related components
 */
export const selectItem = (item: PurchaseItem): { type: string, payload: PurchaseItem } => {
  return {
    type: SELECT_ITEM_REQUEST,
    payload: item,
  };
};

export const updateItem = (item: PurchaseItem): IAction<State> => {
  return {
    type: UPDATE_ITEM,
    payload: ItemToState(item),
  };
};

export const replaceItem = (replace: PurchaseItem, replacement: PurchaseItem): IAction<any> => {
  return {
    type: REPLACE_ITEM,
    payload: {
      replace: ItemToState(replace),
      replacement: ItemToState(replacement)
    },
  };
};

export const proceedToPayment = (): { type: string } => {
  return {type: PROCEED_TO_PAYMENT};
};

export const getContactNodeFromBackend = (contact: Contact, uncheckItems = false): IAction<{contact: Contact, uncheckItems?: boolean}> => {
  return {
    type: GET_CONTACT_NODE_AND_MODEL_FROM_BACKEND,
    payload: {contact, uncheckItems},
  };
};

export const getAllContactNodesFromBackend = (): IAction<Contact[]> => {
  return {
    type: GET_ALL_CONTACT_NODES_FROM_BACKEND,
  };
};


/**
 * the node has been got from backend and we need to update client state
 */
export const addContactNodeToState = (node: ContactNode): IAction<State> => {
  const payload: State = ContactNodeToState([node]);
  return {
    payload,
    type: ADD_CONTACT_NODE_TO_STATE,
  };
};

export const rewriteContactNodeToState = (node: ContactNode): IAction<State> => {
  const payload: State = ContactNodeToState([node]);
  return {
    payload,
    type: REWRITE_CONTACT_NODE_TO_STATE,
  };
};

export const rewriteContactNodesToState = (nodes: ContactNode[]): IAction<State> => {
  const payload: State = ContactNodeToState(nodes);
  return {
    payload,
    type: REWRITE_CONTACT_NODES_TO_STATE,
  };
};

export const updateEnrolmentFields = (form, type) => {
  return {
    type: UPDATE_ENROLMENT_FIELDS,
    payload: {form, type},
  };
};

export const removeItemFromSummary = (type, id) => {
  return {
    type: REMOVE_ITEM_FROM_SUMMARY,
    payload: {type, id},
  };
};

export const removeContactFromSummary = contactId => {
  return {
    type: REMOVE_CONTACT_FROM_SUMMARY,
    payload: {contactId},
  };
};
