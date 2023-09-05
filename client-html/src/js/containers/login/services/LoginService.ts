import { AuthenticationApi, LoginRequest, LoginResponse, PasswordComplexity, User, UserApi } from "@api/model";
import axios from "axios";
import { CONTEXT } from "../../../common/api/Constants";
import { DefaultHttpService } from "../../../common/services/HttpService";

const instance = axios.create();
instance.defaults.baseURL = CONTEXT;

class LoginService {
  // workaround for LoginApp bundle
  private createCustomAPI(type: string, host?: string, port?: number): any {
    if (host && port) {
      instance.defaults.baseURL = `https://${host}:${port}${CONTEXT}`;
    }

    if (type === "user") {
      return new UserApi(new DefaultHttpService(instance));
    }

    return new AuthenticationApi(new DefaultHttpService(instance));
  }

  public postLoginRequest(body: LoginRequest, host?: string, port?: number): Promise<LoginResponse> {
    if (host && port) {
      return this.createCustomAPI("login", host, port).login(body);
    }

    return this.createCustomAPI("login", null, null).login(body);
  }

  public checkPassword(value: string, host?: string, port?: number): Promise<PasswordComplexity> {
    if (host && port) {
      return this.createCustomAPI("user", host, port).checkPassword(encodeURIComponent(value));
    }

    return this.createCustomAPI("user").checkPassword(encodeURIComponent(value));
  }

  public updatePassword(value: string): Promise<LoginResponse> {
    return this.createCustomAPI("user").updatePassword(encodeURIComponent(value));
  }

  public getEmailByToken(token: string): Promise<LoginResponse> {
    return this.createCustomAPI("user").getUserEmailByInvitation(token);
  }

  public createPasswordByToken(token: string, password: string): Promise<LoginResponse> {
    return this.createCustomAPI("user").createPassword(token, password);
  }

  public getUser(): Promise<User> {
    return this.createCustomAPI("login").getUser();
  }

  public getSsoLink(type: string): Promise<string> {
    return this.createCustomAPI("login").getSsoLink(type);
  }
}

export default new LoginService();
