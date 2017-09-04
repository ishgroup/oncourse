import {DefaultHttpService, HttpService} from "../common/services/HttpService";

export class MenuApi {
  private http = new DefaultHttpService();

  getMenuItems(): Promise<any> {
    return this.http.GET('/getMenuItems');
  }
}
