import {_toRejectType, _toRequestType} from "../../../../common/actions/ActionUtils";

export const OpenContactDetailsRequest: string = _toRequestType("checkout/fields/open");

export const FieldsLoad: string = "checkout/fields/load";
export const FieldsLoadRequest: string = _toRequestType(FieldsLoad);
export const FieldsLoadReject: string = _toRejectType(FieldsLoad);

export const FieldsSave: string = "checkout/fields/save";
export const FieldsSaveRequest: string = _toRequestType(FieldsSave);
export const FieldsSaveReject: string = _toRejectType(FieldsSave);

export const FieldsSaved: string = "checkout/fields/saved";
export const FieldsSavedRequest: string = _toRequestType("checkout/fields/saved");
