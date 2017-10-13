import {combineReducers} from "redux";
import {reducer as notifications} from 'react-notification-system-redux';
import {menuReducer} from "../containers/content/containers/menus/reducers";
import {authReducer} from "../containers/auth/reducers";
import {pageReducer} from "../containers/content/containers/pages/reducers";
import {blockReducer} from "../containers/content/containers/blocks/reducers";
import {themesReducer} from "../containers/design/containers/themes/reducers";
import {historyReducer} from "../containers/history/reducers";
import {modalReducer} from "../common/containers/modal/reducers";

import {checkoutSettingsReducer} from "../containers/settings/containers/checkout/reducers";
import {websiteSettingsReducer} from "../containers/settings/containers/website/reducers";
import {redirectSettingsReducer} from "../containers/settings/containers/redirect/reducers";
import {skillsOnCourseReducer} from "../containers/settings/containers/skillsOnCourse/reducers";

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
  settings: combineReducers({
    checkoutSettings: checkoutSettingsReducer,
    websiteSettings: websiteSettingsReducer,
    redirectSettings: redirectSettingsReducer,
    skillsOnCourseSettings: skillsOnCourseReducer,
  }),
});
