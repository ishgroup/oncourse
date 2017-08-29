import {_toRequestType, FULFILLED} from "./ActionUtils";

export const Actions = {
  UPDATE_WILLOW_CONFIG: "common/update/config",

  UPDATE_PREFERENCES_REQUEST: _toRequestType("common/update/preferences"),
  UPDATE_PREFERENCES_REQUEST_FULFILLED: FULFILLED(_toRequestType("common/update/preferences")),
};
