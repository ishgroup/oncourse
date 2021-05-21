import {User} from "../../../model";

export class AuthState {
  isAuthenticated: boolean = false;
  token: string;
  user: User = {} as User;
  credentials: Credentials = {email: "", password: ""};
}

export interface Credentials {
  email: string;
  password: string;
}