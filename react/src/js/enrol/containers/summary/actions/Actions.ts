import {_toRequestType} from "../../../../common/actions/ActionUtils";
import {Enrolment} from "../../../../model/checkout/Enrolment";
import {ContactNode} from "../../../../model/checkout/ContactNode";
import {convert, State} from "../reducers/State";

export const OpenSummaryRequest: string = _toRequestType("checkout/summary/open");
export const PROCEED_TO_PAYMENT: string = "checkout/summary/proceed/to/payment";

export const ItemsLoad: string = "checkout/summary/items/load";
export const ItemsLoadRequest: string = _toRequestType("checkout/summary/items/load");

export const UPDATE_CONTACT_NODE: string = "checkout/summary/update/ContactNode";


export const ItemSelect: string = "checkout/summary/item/select";
export const ItemSelectRequest: string = _toRequestType("checkout/summary/item/select");


export const selectItem = (item: Enrolment, selected: boolean): { type: string, payload: Enrolment } => {
  const payload: Enrolment = {...item, selected: selected};
  return {
    type: ItemSelectRequest,
    payload: payload
  }
};

export const proceedToPayment = (): { type: string } => {
  return {type: PROCEED_TO_PAYMENT}
};

export const updateContactNode = (node: ContactNode): { type: string, payload: State } => {
  return {
    type: UPDATE_CONTACT_NODE,
    payload: convert([node])
  }
};
