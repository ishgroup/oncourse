import {MenuState} from "../containers/content/containers/menus/reducers/State";
import {AuthState} from "../containers/auth/reducers/State";
import {PagesState} from "../containers/content/containers/pages/reducers/State";
import {BlocksState} from "../containers/content/containers/blocks/reducers/State";
import {ThemesState} from "../containers/design/containers/themes/reducers/State";
import {HistoryState} from "../containers/history/reducers/State";

import {RedirectSettingsState} from "../containers/settings/containers/redirect/reducers/State";
import {WebsiteSettingsState} from "../containers/settings/containers/website/reducers/State";
import {SkillsOnCourseState} from "../containers/settings/containers/skillsOnCourse/reducers/State";
import {CheckoutSettingsState} from "../containers/settings/containers/checkout/reducers/State";
import {SpecialPageSettingsState} from "../containers/settings/containers/specialPages/reducers/State";
import {CmsConfig} from "../configLoader";

interface SettingsState {
  redirectSettings: RedirectSettingsState;
  websiteSettings: WebsiteSettingsState;
  specialPageSettings: SpecialPageSettingsState;
  skillsOnCourseSettings: SkillsOnCourseState;
  checkoutSettings: CheckoutSettingsState;
}

// global cms state
export interface State {
  auth: AuthState;
  config: CmsConfig;
  menu: MenuState;
  page: PagesState;
  block: BlocksState;
  theme: ThemesState;
  notifications: any;
  history: HistoryState;
  settings: SettingsState;
  modal: any;
  fetching: boolean;
}

