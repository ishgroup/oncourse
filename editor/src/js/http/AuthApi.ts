import {HttpService} from "../common/services/HttpService";
import {User} from "../model/User";
import {LoginRequest} from "../model/api/LoginRequest";
import {CommonError} from "../model/common/CommonError";

export class AuthApi {
  constructor(private http: HttpService) {
  }

  getUser(loginRequest: LoginRequest): Promise<User> {
    return this.http.POST(`/getUser`, loginRequest);
  }
  logout(): Promise<any> {
    return this.http.POST(`/logout`);
  }
}
