import {_toRequestType, FULFILLED} from "./ActionUtils";

export const Actions = {
  UPDATE_WILLOW_CONFIG: "common/update/config",

  GET_PREFERENCES_REQUEST: _toRequestType("common/update/preferences"),
  GET_PREFERENCES_REQUEST_FULFILLED: FULFILLED(_toRequestType("common/update/preferences")),
};

export const getPreferences = () => ({
  type: Actions.GET_PREFERENCES_REQUEST,
});
