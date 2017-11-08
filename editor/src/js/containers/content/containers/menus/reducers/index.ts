import {IAction} from "../../../../../actions/IshAction";
import {MenuState} from "./State";
import {
  CHANGE_MENU_TREE, GET_MENU_ITEMS_FULFILLED, GET_MENU_ITEMS_REQUEST, SAVE_MENU_TREE_FULFILLED,
  SAVE_MENU_TREE_REQUEST
} from "../actions";

export const menuReducer = (state: MenuState = new MenuState(), action: IAction<any>): MenuState => {
  switch (action.type) {

    case GET_MENU_ITEMS_REQUEST:
    case SAVE_MENU_TREE_REQUEST: {
      return {
        ...state,
        fetching: true,
      };
    }

    case SAVE_MENU_TREE_FULFILLED: {
      return {
        ...state,
        fetching: false,
      };
    }

    case GET_MENU_ITEMS_FULFILLED:
      return {
        ...state,
        fetching: false,
        items: action.payload,
      };

    case CHANGE_MENU_TREE:
      return {
        ...state,
        items: action.payload,
      };


    default:
      return state;
  }
};
