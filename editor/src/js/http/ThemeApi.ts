import {HttpService} from "../common/services/HttpService";
import {Layout} from "../model/Layout";
import {Theme} from "../model/Theme";
import {CommonError} from "../model/common/CommonError";

export class ThemeApi {
  constructor(private http: HttpService) {
  }

  layoutListGet(): Promise<Layout[]> {
    return this.http.GET(`/layout.list`);
  }
  themeCreatePost(): Promise<Theme> {
    return this.http.POST(`/theme.create`);
  }
  themeDeleteIdPost(id: string): Promise<any> {
    return this.http.POST(`/theme.delete/${id}`);
  }
  themeListGet(): Promise<Theme[]> {
    return this.http.GET(`/theme.list`);
  }
  themeUpdatePost(saveThemeRequest: Theme): Promise<Theme> {
    return this.http.POST(`/theme.update`, saveThemeRequest);
  }
}
