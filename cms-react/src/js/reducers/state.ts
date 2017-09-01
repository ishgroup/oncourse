import {User} from "../model";

export interface State {
  auth: AuthState;
}

export class AuthState {
  isAuthenticated: boolean = false;
  token: string;
  user: User;
}
