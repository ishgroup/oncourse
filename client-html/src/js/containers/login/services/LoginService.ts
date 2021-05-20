import axios from "axios";
import { CONTEXT } from "../../../common/api/Constants";
import { DefaultHttpService } from "../../../common/services/HttpService";
import {
  AuthenticationApi,
  UserApi,
  LoginRequest,
  LoginResponse,
  PasswordComplexity
} from "@api/model";

const instance = axios.create();
instance.defaults.baseURL = CONTEXT;
instance.defaults.headers = { "Ish-JXBrowser-Header": "" };

class LoginService {
  // workaround for LoginApp bundle
  private createCustomAPI(type: string, host?: string, port?: number, login?: string): any {
    if (host && port) {
      instance.defaults.baseURL = `https://${host}:${port}${CONTEXT}`;
    }

    if (login) {
      instance.defaults.headers["Ish-JXBrowser-Header"] = encodeURIComponent(login);
    }

    if (type === "user") {
      return new UserApi(new DefaultHttpService(instance));
    }

    return new AuthenticationApi(new DefaultHttpService(instance));
  }

  public postLoginRequest(body: LoginRequest, host?: string, port?: number): Promise<LoginResponse> {
    if (host && port) {
      return this.createCustomAPI("login", host, port, body.login).login(body);
    }

    return this.createCustomAPI("login", null, null, body.login).login(body);
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
}

export default new LoginService();
