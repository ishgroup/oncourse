import {User} from "../../../model";

export class AuthState {
  isAuthenticated: boolean = false;
  token: string;
  user: User;
}
