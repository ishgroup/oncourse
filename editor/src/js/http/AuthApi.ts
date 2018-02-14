import {HttpService} from "../common/services/HttpService";
import {User} from "../model/User";
import {LoginRequest} from "../model/api/LoginRequest";
import {CommonError} from "../model/common/CommonError";

export class AuthApi {
  constructor(private http: HttpService) {
  }

  getUser(): Promise<User> {
    return this.http.GET(`/getUser`);
  }
  login(loginRequest: LoginRequest): Promise<any> {
    return this.http.POST(`/login`, loginRequest);
  }
  logout(): Promise<any> {
    return this.http.POST(`/logout`);
  }
}
