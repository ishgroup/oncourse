import {DefaultHttpService} from "../common/services/HttpService";
import {API} from "../constants/Config";

export class ThemeApi {
  private http = new DefaultHttpService();

  getThemes(): Promise<any> {
    return this.http.GET(API.GET_THEMES);
  }

  saveTheme(payload): Promise<any> {
    return this.http.POST(API.SAVE_THEME, payload);
  }

  addTheme(): Promise<any> {
    return this.http.POST(API.ADD_THEME);
  }

  deleteTheme(id): Promise<any> {
    return this.http.POST(API.DELETE_THEME, id);
  }
}
