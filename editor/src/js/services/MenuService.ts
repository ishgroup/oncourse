import {MenuApi} from "../http/MenuApi";
import {MenuItem} from "../model";
import {DefaultHttpService} from "../common/services/HttpService";

class MenuService {
  readonly menuApi = new MenuApi(new DefaultHttpService());

  public getMenuItems(): Promise<MenuItem[]> {
    return this.menuApi.getMenuItems();
  }

  public saveMenuItems(items: MenuItem[]): Promise<MenuItem[]> {
    return this.menuApi.saveMenuItems(items);
  }
}

export default new MenuService;
