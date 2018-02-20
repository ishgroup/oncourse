import {AuthApi} from "../http/AuthApi";
import {LoginRequest, User} from "../model";
import {DefaultHttpService} from "../common/services/HttpService";

class AuthService {
  readonly authApi = new AuthApi(new DefaultHttpService());

  public getUser(): Promise<User> {
    return this.authApi.userGetGet();
  }
  
  public login(values: LoginRequest): Promise<any> {
    return this.authApi.userLoginPost(values);
  }
  
  public logout(): Promise<any> {
    return this.authApi.userLogoutPost();
  }
}

export default new AuthService;
