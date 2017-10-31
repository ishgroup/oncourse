import {AuthApi} from "../http/AuthApi";
import {LoginRequest} from "../model";

class AuthService {
  readonly authApi = new AuthApi();

  public submitUser(values: LoginRequest): any {
    return this.authApi.submitUser(values);
  }

  public logout(): any {
    return this.authApi.logout();
  }
}

export default new AuthService;
