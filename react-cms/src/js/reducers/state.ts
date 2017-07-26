export class State {
  auth: AuthState = new AuthState();
}

class AuthState {
  isAuthenticated: boolean = true;
  token: string;
}
