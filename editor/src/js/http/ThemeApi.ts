import {HttpService} from "../common/services/HttpService";
import {Layout} from "../model/Layout";
import {Theme} from "../model/Theme";
import {CommonError} from "../model/common/CommonError";

export class ThemeApi {
  constructor(private http: HttpService) {
  }

  addTheme(): Promise<Theme> {
    return this.http.POST(`/addTheme`);
  }
  deleteTheme(themeName: string): Promise<any> {
    return this.http.POST(`/deleteTheme/${themeName}`);
  }
  getLayouts(): Promise<Layout[]> {
    return this.http.GET(`/getLayouts`);
  }
  getThemes(): Promise<Theme[]> {
    return this.http.GET(`/getThemes`);
  }
  saveTheme(saveThemeRequest: Theme): Promise<Theme> {
    return this.http.POST(`/saveTheme`, saveThemeRequest);
  }
}
