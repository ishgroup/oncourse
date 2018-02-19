import {HttpService} from "../common/services/HttpService";
import {LoginRequest} from "../model/LoginRequest";
import {User} from "../model/User";
import {CommonError} from "../model/common/CommonError";

export class AuthApi {
  constructor(private http: HttpService) {
  }

  getUser(): Promise<User> {
    return this.http.GET(`/user.get`);
  }
  login(loginRequest: LoginRequest): Promise<any> {
    return this.http.POST(`/user.login`, loginRequest);
  }
  logout(): Promise<any> {
    return this.http.POST(`/user.logout`);
  }
}
