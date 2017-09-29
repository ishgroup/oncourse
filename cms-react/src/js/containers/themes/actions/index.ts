import {_toRequestType, FULFILLED} from "../../../common/actions/ActionUtils";

export const GET_THEMES_REQUEST = _toRequestType("themes/get/items");
export const GET_THEMES_FULFILLED = FULFILLED(GET_THEMES_REQUEST);

export const SAVE_THEME_REQUEST = _toRequestType("themes/save/theme");
export const SAVE_THEME_FULFILLED = FULFILLED(SAVE_THEME_REQUEST);

export const DELETE_THEME_REQUEST = _toRequestType("themes/delete/theme");
export const DELETE_THEME_FULFILLED = FULFILLED(DELETE_THEME_REQUEST);

export const getThemes = () => ({
  type: GET_THEMES_REQUEST,
});

export const saveTheme = (id, props) => ({
  type: SAVE_THEME_REQUEST,
  payload: {id, ...props},
});

export const deleteTheme = id => ({
  type: DELETE_THEME_REQUEST,
  payload: id,
});
