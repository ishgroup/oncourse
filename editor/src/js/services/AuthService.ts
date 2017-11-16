import {AuthApi} from "../http/AuthApi";
import {LoginRequest, User} from "../model";
import {DefaultHttpService} from "../common/services/HttpService";

class AuthService {
  readonly authApi = new AuthApi(new DefaultHttpService());

  public submitUser(values: LoginRequest): Promise<User> {
    return this.authApi.getUser(values);
  }

  public logout(): Promise<any> {
    return this.authApi.logout();
  }
}

export default new AuthService;
