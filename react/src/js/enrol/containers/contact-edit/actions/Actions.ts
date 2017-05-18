import {normalize} from "normalizr";

import {_toRejectType, _toRequestType} from "../../../../common/actions/ActionUtils";
export const FieldsLoad: string = "checkout/fields/load";
export const FieldsLoadRequest: string = _toRequestType(FieldsLoad);
export const FieldsLoadReject: string = _toRejectType(FieldsLoad);

export const FieldsSave: string = "checkout/fields/save";
export const FieldsSaveRequest: string = _toRequestType(FieldsSave);
export const FieldsSaveReject: string = _toRejectType(FieldsSave);
