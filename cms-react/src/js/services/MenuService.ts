import {MenuApi} from "../http/MenuApi";
import {MenuItem} from "../model";

class MenuService {
  readonly menuApi = new MenuApi();

  public getMenuItems(): Promise<MenuItem[]> {
    return this.menuApi.getMenuItems();
  }
}

export default new MenuService;
