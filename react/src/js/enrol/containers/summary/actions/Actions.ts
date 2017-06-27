import {_toRequestType} from "../../../../common/actions/ActionUtils";
import {ContactNode} from "../../../../model/checkout/ContactNode";
import {ContactNodeToState, State} from "../reducers/State";
import {IAction} from "../../../../actions/IshAction";
import {Contact} from "../../../../model/web/Contact";
import {PurchaseItem} from "../../../../model/checkout/Index";
import {Voucher} from "../../../../model/checkout/Voucher";

export const ADD_CONTACT_TO_SUMMARY: string = _toRequestType("checkout/summary/add/contact/to/summary");

export const PROCEED_TO_PAYMENT: string = "checkout/summary/proceed/to/payment";

export const ItemsLoad: string = "checkout/summary/items/load";

export const GET_CONTACT_NODE_FROM_BACKEND: string = "checkout/summary/get/ContactNode/from/backend";
export const ADD_CONTACT_NODE_TO_STATE: string = "checkout/summary/update/ContactNode";


export const SELECT_ITEM: string = "checkout/summary/select/item";
export const SELECT_ITEM_REQUEST: string = _toRequestType(SELECT_ITEM);

export const UPDATE_ITEM: string = "checkout/summary/update/item";


export const addContactToSummary = (contact: Contact): IAction<Contact> => {
  return {
    type: ADD_CONTACT_TO_SUMMARY,
    payload: contact,
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

export const updateItem = (item: PurchaseItem): { type: string, payload: PurchaseItem } => {
  return {
    type: UPDATE_ITEM,
    payload: item,
  };
};

export const proceedToPayment = (): { type: string } => {
  return {type: PROCEED_TO_PAYMENT};
};

export const getContactNodeFromBackend = (contact: Contact): IAction<Contact> => {
  return {
    type: GET_CONTACT_NODE_FROM_BACKEND,
    payload: contact,
  };
};

/**
 * the node has been got from backend and we need to update client state
 */
export const addContactNodeToState = (node: ContactNode): IAction<State> => {
  const payload: State = ContactNodeToState([node]);
  return {
    type: ADD_CONTACT_NODE_TO_STATE,
    payload,
  };
};
