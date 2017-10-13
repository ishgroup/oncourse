import {IAction} from "../../../../../actions/IshAction";
import {MenuState} from "./State";
import {CHANGE_MENU_TREE, GET_MENU_ITEMS_FULFILLED} from "../actions";

export const menuReducer = (state: MenuState = new MenuState(), action: IAction<any>): MenuState => {
  switch (action.type) {

    case GET_MENU_ITEMS_FULFILLED:
      return {
        ...state,
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
