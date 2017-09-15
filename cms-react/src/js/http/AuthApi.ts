import {DefaultHttpService, HttpService} from "../common/services/HttpService";
import {API} from "../constants/Config";

export class AuthApi {
  private http = new DefaultHttpService();

  submitUser(values: any): Promise<any> {
    return this.http.POST(API.LOGIN, values);
  }

  logout(): Promise<any> {
    return this.http.POST(API.LOGOUT);
  }
}
