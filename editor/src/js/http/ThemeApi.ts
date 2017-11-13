import {DefaultHttpService} from "../common/services/HttpService";
import {API} from "../constants/Config";
import {Layout, Theme} from "../model";

export class ThemeApi {
  private http = new DefaultHttpService();

  getThemes(): Promise<Theme[]> {
    return this.http.GET(API.GET_THEMES);
  }

  saveTheme(payload): Promise<any> {
    return this.http.POST(API.SAVE_THEME, payload);
  }

  addTheme(): Promise<Theme> {
    return this.http.POST(API.ADD_THEME);
  }

  deleteTheme(id): Promise<any> {
    return this.http.POST(API.DELETE_THEME, id);
  }

  getLayouts(): Promise<Layout[]> {
    return this.http.GET(API.GET_LAYOUTS);
  }
}
