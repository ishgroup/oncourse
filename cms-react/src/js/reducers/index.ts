import {combineReducers} from "redux";
import {menuReducer} from "../containers/menus/reducers";
import {authReducer} from "../containers/auth/reducers";
import {pageReducer} from "../containers/pages/reducers";
import {configReducer} from "../common/reducers";

export const combinedReducers = combineReducers({
  auth: authReducer,
  menu: menuReducer,
  config: configReducer,
  page: pageReducer,
});
