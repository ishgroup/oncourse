import {MenuState} from "../containers/menus/reducers/State";
import {AuthState} from "../containers/auth/reducers/State";
import {PagesState} from "../containers/pages/reducers/State";
import {BlocksState} from "../containers/blocks/reducers/State";
import {ThemesState} from "../containers/themes/reducers/State";
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

