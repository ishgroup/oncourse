import {combineReducers} from "redux";
import {reducer as reduxFormReducer} from 'redux-form';

import {menuReducer} from "../containers/content/containers/menus/reducers";
import {authReducer} from "../containers/auth/reducers";
import {pageReducer} from "../containers/content/containers/pages/reducers";
import {blockReducer} from "../containers/content/containers/blocks/reducers";
import {themesReducer} from "../containers/design/containers/themes/reducers";
import {historyReducer} from "../containers/history/reducers";
import {modalReducer} from "../common/containers/modal/reducers";
import {navigationReducer} from "../common/containers/Navigation/reducers";
import {messageReducer} from "../common/components/message/reducer";

import {websiteSettingsReducer} from "../containers/settings/containers/website/reducers";
import {redirectSettingsReducer} from "../containers/settings/containers/redirect/reducers";
import {skillsOnCourseReducer} from "../containers/settings/containers/skillsOnCourse/reducers";
import {checkoutSettingsReducer} from "../containers/settings/containers/checkout/reducers";

import {configReducer, fetchReducer} from "../common/reducers";
import {specialPagesReducer} from "../containers/settings/containers/specialPages/reducers";

export const combinedReducers = combineReducers({
  auth: authReducer,
  menu: menuReducer,
  config: configReducer,
  page: pageReducer,
  block: blockReducer,
  theme: themesReducer,
  history: historyReducer,
  modal: modalReducer,
  navigation: navigationReducer,
  message: messageReducer,
  settings: combineReducers({
    websiteSettings: websiteSettingsReducer,
    redirectSettings: redirectSettingsReducer,
    specialPageSettings: specialPagesReducer,
    skillsOnCourseSettings: skillsOnCourseReducer,
    checkoutSettings: checkoutSettingsReducer,
  }),
  form: reduxFormReducer,
  fetching: fetchReducer,
});
