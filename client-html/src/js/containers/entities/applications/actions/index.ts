import { _toRequestType, FULFILLED } from "../../../../common/actions/ActionUtils";
import { Application } from "@api/model";

export const GET_APPLICATION_ITEM = _toRequestType("get/application");
export const GET_APPLICATION_ITEM_FULFILLED = FULFILLED(GET_APPLICATION_ITEM);

export const DELETE_APPLICATION_ITEM = _toRequestType("delete/application");
export const DELETE_APPLICATION_ITEM_FULFILLED = FULFILLED(DELETE_APPLICATION_ITEM);

export const UPDATE_APPLICATION_ITEM = _toRequestType("put/application");
export const UPDATE_APPLICATION_ITEM_FULFILLED = FULFILLED(UPDATE_APPLICATION_ITEM);

export const CREATE_APPLICATION_ITEM = _toRequestType("post/application");
export const CREATE_APPLICATION_ITEM_FULFILLED = FULFILLED(CREATE_APPLICATION_ITEM);

export const GET_APPLICATION_DELETE_VALIDATION = _toRequestType("get/list/entity/application/validation");
export const GET_APPLICATION_DELETE_VALIDATION_FULFILLED = FULFILLED(GET_APPLICATION_DELETE_VALIDATION);

export const validateDeleteApplication = (id: string, callback: any) => ({
  type: GET_APPLICATION_DELETE_VALIDATION,
  payload: { id, callback }
});

export const getApplication = (id: string) => ({
  type: GET_APPLICATION_ITEM,
  payload: id
});

export const removeApplication = (id: string) => ({
  type: DELETE_APPLICATION_ITEM,
  payload: id
});

export const updateApplication = (id: string, application: Application) => ({
  type: UPDATE_APPLICATION_ITEM,
  payload: { id, application }
});

export const createApplication = (application: Application) => ({
  type: CREATE_APPLICATION_ITEM,
  payload: { application }
});
