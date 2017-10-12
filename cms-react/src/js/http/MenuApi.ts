import {DefaultHttpService, HttpService} from "../common/services/HttpService";
import {API} from "../constants/Config";

export class MenuApi {
  private http = new DefaultHttpService();

  getMenuItems(): Promise<any> {
    return this.http.GET(API.GET_MENU);
  }

  saveMenuItems(items): Promise<any> {
    return this.http.POST(API.SAVE_MENU, {items});
  }
}
