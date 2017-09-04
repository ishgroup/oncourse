import {_toRequestType, FULFILLED} from "../../../common/actions/ActionUtils";

export const GET_MENU_ITEMS_REQUEST = _toRequestType("menus/get/items");
export const GET_MENU_ITEMS_FULFILLED = FULFILLED(GET_MENU_ITEMS_REQUEST);

export const CHANGE_MENU_TREE = "menus/change/tree";

export const getMenuItems = () => ({
  type: GET_MENU_ITEMS_REQUEST,
})

export const changeMenuTree = treeData => ({
  type: CHANGE_MENU_TREE,
  payload: treeData,
})
