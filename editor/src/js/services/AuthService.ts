import {AuthApi} from "../http/AuthApi";
import {LoginRequest, User} from "../model";

class AuthService {
  readonly authApi = new AuthApi();

  public submitUser(values: LoginRequest): Promise<User> {
    return this.authApi.submitUser(values);
  }

  public logout(): Promise<any> {
    return this.authApi.logout();
  }
}

export default new AuthService;
