import {combineReducers} from "redux";
import {reducer as notifications} from 'react-notification-system-redux';
import {menuReducer} from "../containers/content/containers/menus/reducers";
import {authReducer} from "../containers/auth/reducers";
import {pageReducer} from "../containers/content/containers/pages/reducers";
import {blockReducer} from "../containers/content/containers/blocks/reducers";
import {themesReducer} from "../containers/design/containers/themes/reducers";
import {historyReducer} from "../containers/history/reducers";
import {modalReducer} from "../common/containers/modal/reducers";
import {configReducer} from "../common/reducers";

export const combinedReducers = combineReducers({
  notifications,
  auth: authReducer,
  menu: menuReducer,
  config: configReducer,
  page: pageReducer,
  block: blockReducer,
  theme: themesReducer,
  history: historyReducer,
  modal: modalReducer,
});
