import {DefaultHttpService} from "../common/services/HttpService";
import {API} from "../constants/Config";

export class ThemeApi {
  private http = new DefaultHttpService();

  getThemes(): Promise<any> {
    return this.http.GET(API.GET_THEMES);
  }
}
