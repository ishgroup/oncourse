import {_toRequestType, FULFILLED} from "../../../../../common/actions/ActionUtils";
import {Theme} from "../../../../../model";

export const GET_THEMES_REQUEST = _toRequestType("themes/get/items");
export const GET_THEMES_FULFILLED = FULFILLED(GET_THEMES_REQUEST);

export const GET_LAYOUTS_REQUEST = _toRequestType("themes/get/layouts");
export const GET_LAYOUTS_FULFILLED = FULFILLED(GET_LAYOUTS_REQUEST);

export const SAVE_THEME_REQUEST = _toRequestType("themes/save/theme");
export const SAVE_THEME_FULFILLED = FULFILLED(SAVE_THEME_REQUEST);

export const ADD_THEME_REQUEST = _toRequestType("themes/add/theme");
export const ADD_THEME_FULFILLED = FULFILLED(ADD_THEME_REQUEST);

export const DELETE_THEME_REQUEST = _toRequestType("themes/delete/theme");
export const DELETE_THEME_FULFILLED = FULFILLED(DELETE_THEME_REQUEST);

export const UPDATE_THEME_STATE = "themes/update/theme/state";


export const getThemes = () => ({
  type: GET_THEMES_REQUEST,
});

export const getLayouts = () => ({
  type: GET_LAYOUTS_REQUEST,
});

export const saveTheme = (theme) => ({
  type: SAVE_THEME_REQUEST,
  payload: theme,
});

export const addTheme = () => ({
  type: ADD_THEME_REQUEST,
});

export const updateThemeState = (theme: Theme) => ({
  type: UPDATE_THEME_STATE,
  payload: theme,
});

export const deleteTheme = id => ({
  type: DELETE_THEME_REQUEST,
  payload: id,
});

