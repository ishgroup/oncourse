import { AuthenticationApi, LoginRequest } from '@api/model';
import { DefaultHttpService } from '../constants/HttpService';

class LoginService {
  private loginApi = new AuthenticationApi(new DefaultHttpService());

  public signIn(details: LoginRequest): Promise<any> {
    return this.loginApi.signIn(details);
  }

  public signUp(details: LoginRequest): Promise<any> {
    return this.loginApi.signUp(details);
  }
}

export default new LoginService();
