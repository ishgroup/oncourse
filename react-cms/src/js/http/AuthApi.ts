import {DefaultHttpService, HttpService} from "../common/services/HttpService";

export class AuthApi {
  private http = new DefaultHttpService();

  submitUser(values: any): Promise<any> {
    return this.http.POST('/getUser', values);
  }
}
