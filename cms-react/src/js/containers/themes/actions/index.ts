import {_toRequestType, FULFILLED} from "../../../common/actions/ActionUtils";

export const GET_THEMES_REQUEST = _toRequestType("themes/get/items");
export const GET_THEMES_FULFILLED = FULFILLED(GET_THEMES_REQUEST);


export const getThemes = () => ({
  type: GET_THEMES_REQUEST,
});

