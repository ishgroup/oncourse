import { _toRequestType, FULFILLED } from "../../../../common/actions/ActionUtils";
import { Application } from "@api/model";

export const UPDATE_APPLICATION_ITEM = _toRequestType("put/application");
export const UPDATE_APPLICATION_ITEM_FULFILLED = FULFILLED(UPDATE_APPLICATION_ITEM);

export const updateApplication = (id: string, application: Application) => ({
  type: UPDATE_APPLICATION_ITEM,
  payload: { id, application }
});
