import { _toRequestType } from "../../../common/actions/ActionUtils";

export const GET_FINALISE_INFO = _toRequestType("get/finalise/info");

export const UPDATE_FINALISE_DATE = _toRequestType("post/finalise/date");

export const getFinaliseInfo = (lockDate: string) => ({
  type: GET_FINALISE_INFO,
  payload: lockDate
});

export const updateFinaliseDate = (lockDate: string) => ({
  type: UPDATE_FINALISE_DATE,
  payload: lockDate
});
