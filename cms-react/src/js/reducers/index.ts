import {combineReducers} from "redux";
import {menuReducer} from "../containers/menus/reducers";
import {authReducer} from "../containers/auth/reducers";

export const combinedReducers = combineReducers({
  auth: authReducer,
  menu: menuReducer,
});
