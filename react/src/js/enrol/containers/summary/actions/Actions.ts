import {_toRequestType} from "../../../../common/actions/ActionUtils";
import {Enrolment} from "../../../../model/checkout/Enrolment";

export const OpenSummaryRequest: string = _toRequestType("checkout/summary/open");

export const ItemsLoad: string = "checkout/summary/items/load";
export const ItemsLoadRequest: string = _toRequestType("checkout/summary/items/load");

export const ItemSelect: string = "checkout/summary/item/select";
export const ItemSelectRequest: string = _toRequestType("checkout/summary/item/select");


export const selectItem = (item: Enrolment, selected: boolean): { type: string, payload: any } => {
  return {
    type: ItemSelectRequest,
    payload: {...item, selected: selected}
  }
};
