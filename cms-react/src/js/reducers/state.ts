import {User} from "../model";
import {MenuState} from "../containers/menus/reducers/State";
import {AuthState} from "../containers/auth/reducers/State";


// global cms state
export interface State {
  auth: AuthState;
  menu: MenuState;
}

