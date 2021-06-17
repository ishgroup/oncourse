import {
  AuthenticationApi, LoginRequest, LoginResponse, TokenResponse
} from '@api/model';
import { DefaultHttpService } from '../constants/HttpService';

class LoginService {
  private loginApi = new AuthenticationApi(new DefaultHttpService());

  public signIn(details: LoginRequest): Promise<LoginResponse> {
    return this.loginApi.signIn(details);
  }

  public signUp(details: LoginRequest): Promise<LoginResponse> {
    return this.loginApi.signUp(details);
  }

  public connect(tokenResponse: TokenResponse): Promise<LoginResponse> {
    return this.loginApi.connect(tokenResponse);
  }
}

export default new LoginService();
