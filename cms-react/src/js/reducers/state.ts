import {MenuState} from "../containers/content/containers/menus/reducers/State";
import {AuthState} from "../containers/auth/reducers/State";
import {PagesState} from "../containers/content/containers/pages/reducers/State";
import {BlocksState} from "../containers/content/containers/blocks/reducers/State";
import {ThemesState} from "../containers/design/containers/themes/reducers/State";
import {HistoryState} from "../containers/history/reducers/State";

// global cms state
export interface State {
  auth: AuthState;
  menu: MenuState;
  page: PagesState;
  block: BlocksState;
  theme: ThemesState;
  notifications: any;
  history: HistoryState;
}

