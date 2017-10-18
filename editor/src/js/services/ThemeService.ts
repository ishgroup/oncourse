import {ThemeApi} from "../http/ThemeApi";
import {Theme} from "../model";
import {State} from "../reducers/state";

class ThemeService {
  readonly themeApi = new ThemeApi();

  public getThemes(): Promise<Theme[]> {
    return this.themeApi.getThemes();
  }

  public saveTheme(props, state: State): Promise<Theme[]> {
    return this.themeApi.saveTheme(this.buildSaveThemeRequest(props, state));
  }

  public deleteTheme(id): Promise<Theme[]> {
    return this.themeApi.deleteTheme(id);
  }

  public buildSaveThemeRequest(props, state: State) {
    const theme = state.theme.items.find(t => t.id === props.id);
    return {
      ...theme,
      ...props,
      id: props.id != -1 ? props.id : null,
    };
  }

}

export default new ThemeService();
