import {AuthApi} from "../../../build/generated-sources";
import {LoginRequest, User} from "../model";
import {DefaultHttpService} from "../common/services/HttpService";

class AuthService {
  readonly authApi = new AuthApi(new DefaultHttpService());

  public getUser(): Promise<User> {
    return this.authApi.getUser();
  }
  
  public login(values: LoginRequest): Promise<any> {
    return this.authApi.createSession(values);
  }
  
  public logout(): Promise<any> {
    return this.authApi.destroySession();
  }
}

export default new AuthService;
