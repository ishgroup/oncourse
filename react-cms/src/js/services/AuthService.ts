import {AuthApi} from "../http/AuthApi";

class AuthService {
  readonly authApi = new AuthApi();

  public submitUser (values: any): any {
    return this.authApi.submitUser(values);
  }
}

export default new AuthService;
