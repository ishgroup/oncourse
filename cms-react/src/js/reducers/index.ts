import {combineReducers} from "redux";
import {menuReducer} from "../containers/menus/reducers";
import {authReducer} from "../containers/auth/reducers";
import {configReducer} from "../common/reducers/index";
import {pageReducer} from "../containers/pages/reducers/index";

export const combinedReducers = combineReducers({
  auth: authReducer,
  menu: menuReducer,
  config: configReducer,
  page: pageReducer,
});
