import {_toRequestType} from "../../../../common/actions/ActionUtils";
import {Enrolment} from "../../../../model/checkout/Enrolment";
import {ContactNode} from "../../../../model/checkout/ContactNode";
import {ContactNodeToState, State} from "../reducers/State";
import {Application} from "../../../../model/checkout/Application";

export const OpenSummaryRequest: string = _toRequestType("checkout/summary/open");
export const PROCEED_TO_PAYMENT: string = "checkout/summary/proceed/to/payment";

export const ItemsLoad: string = "checkout/summary/items/load";
export const ItemsLoadRequest: string = _toRequestType(ItemsLoad);

export const UPDATE_CONTACT_NODE: string = "checkout/summary/update/ContactNode";


export const SELECT_ITEM: string = "checkout/summary/select/item";
export const SELECT_ITEM_REQUEST: string = _toRequestType(SELECT_ITEM);


/**
 * an user has selected/unselected this item and we need to update all related components
 */
export const selectItem = (item: Enrolment | Application, selected: boolean): { type: string, payload: Enrolment | Application } => {
  item.selected = selected;
  return {
    type: SELECT_ITEM_REQUEST,
    payload: item
  }
};

export const proceedToPayment = (): { type: string } => {
  return {type: PROCEED_TO_PAYMENT}
};

/**
 * the node has been got from backend and we need to update client state
 */
export const updateContactNode = (node: ContactNode): { type: string, payload: State } => {
  const payload:State = ContactNodeToState([node]);
  return {
    type: UPDATE_CONTACT_NODE,
    payload: payload
  }
};
