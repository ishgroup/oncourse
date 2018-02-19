import {HttpService} from "../common/services/HttpService";
import {Layout} from "../model/Layout";
import {Theme} from "../model/Theme";
import {CommonError} from "../model/common/CommonError";

export class ThemeApi {
  constructor(private http: HttpService) {
  }

  addTheme(): Promise<Theme> {
    return this.http.POST(`/theme.create`);
  }
  deleteTheme(id: string): Promise<any> {
    return this.http.POST(`/theme.delete/${id}`);
  }
  getLayouts(): Promise<Layout[]> {
    return this.http.GET(`/layout.list`);
  }
  getThemes(): Promise<Theme[]> {
    return this.http.GET(`/theme.list`);
  }
  saveTheme(saveThemeRequest: Theme): Promise<Theme> {
    return this.http.POST(`/theme.update`, saveThemeRequest);
  }
}
