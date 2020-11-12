import { _toRequestType, FULFILLED } from "../../../../common/actions/ActionUtils";

export const GET_CUSTOM_FIELD_TYPES = _toRequestType("get/customFieldTypes");
export const GET_CUSTOM_FIELD_TYPES_FULFILLED = FULFILLED(GET_CUSTOM_FIELD_TYPES);

export const getCustomFieldTypes = (entity: string) => ({
  type: GET_CUSTOM_FIELD_TYPES,
  payload: entity
});
