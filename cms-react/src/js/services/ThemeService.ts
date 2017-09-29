import {ThemeApi} from "../http/ThemeApi";
import {Theme} from "../model";

class ThemeService {
  readonly themeApi = new ThemeApi();

  public getThemes(): Promise<Theme[]> {
    console.log('api');
    return this.themeApi.getThemes();
  }

}

export default new ThemeService();
