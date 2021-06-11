import {AuthenticationApi, LoginRequest} from "@api/model";
import {DefaultHttpService} from "../constants/HttpService";


class LoginService {
  private loginApi = new AuthenticationApi(new DefaultHttpService());

  public login(details: LoginRequest): Promise<any> {
    return this.loginApi.login(details);
  }
}

export default new LoginService();
