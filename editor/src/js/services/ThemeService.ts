import {ThemeApi} from "../http/ThemeApi";
import {Theme, Layout} from "../model";
import {State} from "../reducers/state";
import {DefaultHttpService} from "../common/services/HttpService";

class ThemeService {
  readonly themeApi = new ThemeApi(new DefaultHttpService());

  public getThemes(): Promise<Theme[]> {
    return this.themeApi.getThemes();
  }

  public getLayouts(): Promise<Layout[]> {
    return this.themeApi.getLayouts();
  }

  public saveTheme(props, state: State): Promise<Theme> {
    return this.themeApi.saveTheme(this.buildSaveThemeRequest(props, state));
  }

  public addTheme(): Promise<Theme> {
    return this.themeApi.addTheme();
  }

  public deleteTheme(id): Promise<any> {
    return this.themeApi.deleteTheme(id);
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
