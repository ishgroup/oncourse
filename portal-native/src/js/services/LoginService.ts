import { AuthenticationApi, LoginRequest, LoginResponse } from '@api/model';
import { DefaultHttpService } from '../constants/HttpService';

class LoginService {
  private loginApi = new AuthenticationApi(new DefaultHttpService());

  public signIn(details: LoginRequest): Promise<LoginResponse> {
    return this.loginApi.login(details);
  }

  public signOut(): Promise<any> {
    return this.loginApi.signOut();
  }

  public emailLogin(email: string): Promise<any> {
    return this.loginApi.verifyEmail(email);
  }

  public ssoClientIds(): Promise<{ [key: string]: string; }> {
    return this.loginApi.ssoClientIds();
  }
}

export default new LoginService();
