import {ThemeApi} from "../../../build/generated-sources/api";

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
    return this.themeApi.updateTheme(this.buildSaveThemeRequest(props, state));
  }

  public addTheme(): Promise<Theme> {
    return this.themeApi.createTheme();
  }

  public deleteTheme(title: string): Promise<any> {
    return this.themeApi.deleteTheme(title);
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
