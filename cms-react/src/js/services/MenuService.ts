import {MenuApi} from "../http/MenuApi";
import {MenuItem} from "../model";

class MenuService {
  readonly menuApi = new MenuApi();

  public getMenuItems(): Promise<MenuItem[]> {
    return this.menuApi.getMenuItems();
  }

  public saveMenuItems(items: MenuItem[]): Promise<MenuItem[]> {
    return this.menuApi.saveMenuItems(items);
  }
}

export default new MenuService;
