import {_toRequestType, FULFILLED} from "../../../../../common/actions/ActionUtils";
import {SpecialPageItem} from "../../../../../../../build/generated-sources";

export const GET_SPECIAL_PAGE_SETTINGS_REQUEST = _toRequestType("settings/get/specialPage");
export const GET_SPECIAL_PAGE_SETTINGS_FULFILLED = FULFILLED(GET_SPECIAL_PAGE_SETTINGS_REQUEST);

export const SET_SPECIAL_PAGE_SETTINGS_REQUEST = _toRequestType("settings/set/specialPage");
export const SET_SPECIAL_PAGE_SETTINGS_FULFILLED = FULFILLED(SET_SPECIAL_PAGE_SETTINGS_REQUEST);

export const getSpecialPageSettings = () => ({
  type: GET_SPECIAL_PAGE_SETTINGS_REQUEST,
});
export const setSpecialPageSettings = (specialPages: SpecialPageItem[]) => ({
  type: SET_SPECIAL_PAGE_SETTINGS_REQUEST,
  payload: specialPages,
});
