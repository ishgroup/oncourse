import {ThemeApi} from "../http/ThemeApi";
import {Theme, Layout} from "../model";
import {State} from "../reducers/state";
import {DefaultHttpService} from "../common/services/HttpService";

class ThemeService {
  readonly themeApi = new ThemeApi(new DefaultHttpService());

  public getThemes(): Promise<Theme[]> {
    return this.themeApi.themeListGet();
  }

  public getLayouts(): Promise<Layout[]> {
    return this.themeApi.layoutListGet();
  }

  public saveTheme(props, state: State): Promise<Theme> {
    return this.themeApi.themeUpdatePost(this.buildSaveThemeRequest(props, state));
  }

  public addTheme(): Promise<Theme> {
    return this.themeApi.themeCreatePost();
  }

  public deleteTheme(title: string): Promise<any> {
    return this.themeApi.themeDeleteIdPost(title);
  }

  public buildSaveThemeRequest(props, state: State) {
    const theme = state.theme.items.find(t => t.id === props.id);
    return {
      ...theme,
      ...props,
    };
  }

}

export default new ThemeService();
