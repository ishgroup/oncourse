import {DefaultHttpService, HttpService} from "../common/services/HttpService";
import {API} from "../constants/Config";
import {LoginRequest, User} from "../model";

export class AuthApi {
  private http = new DefaultHttpService();

  submitUser(values: LoginRequest): Promise<User> {
    return this.http.POST(API.LOGIN, values);
  }

  logout(): Promise<any> {
    return this.http.POST(API.LOGOUT);
  }
}
