import {User} from "../model";
import {MenuState} from "../containers/menus/reducers/State";


// global cms state
export interface State {
  auth: AuthState;
  menu: MenuState;
}

export class AuthState {
  isAuthenticated: boolean = false;
  token: string;
  user: User;
}
