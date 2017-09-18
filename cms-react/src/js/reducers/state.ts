import {MenuState} from "../containers/menus/reducers/State";
import {AuthState} from "../containers/auth/reducers/State";
import {PagesState} from "../containers/pages/reducers/State";

// global cms state
export interface State {
  auth: AuthState;
  menu: MenuState;
  page: PagesState;
}

