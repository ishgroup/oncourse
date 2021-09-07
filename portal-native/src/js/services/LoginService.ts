import {
  AuthenticationApi, LoginRequest, LoginResponse, PasswordComplexity
} from '@api/model';
import { DefaultHttpService } from '../constants/HttpService';
import { ThirdPartyKeysResponse } from '../model/ThirdParty';

class LoginService {
  private loginApi = new AuthenticationApi(new DefaultHttpService());

  public checkPassword(password: string): Promise<PasswordComplexity> {
    return this.loginApi.checkPassword(password);
  }

  public signIn(details: LoginRequest): Promise<LoginResponse> {
    return this.loginApi.login(details);
  }

  public signOut(): Promise<any> {
    return this.loginApi.signOut();
  }

  public emailLogin(email: string): Promise<any> {
    return this.loginApi.verifyEmail(email);
  }

  public ssoClientIds(): Promise<ThirdPartyKeysResponse> {
    return this.loginApi.ssoClientIds();
  }
}

export default new LoginService();
