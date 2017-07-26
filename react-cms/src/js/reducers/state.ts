export class State {
  auth: AuthState = new AuthState();
}

class AuthState {
  isAuthenticated: boolean = false;
  token: string;
}
