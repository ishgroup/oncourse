import {MenuApi} from "../../../build/generated-sources";
import {MenuItem} from "../model";
import {DefaultHttpService} from "../common/services/HttpService";

class MenuService {
  readonly menuApi = new MenuApi(new DefaultHttpService());

  public getMenuItems(): Promise<MenuItem[]> {
    return this.menuApi.getMenus();
  }

  public saveMenuItems(items: MenuItem[]): Promise<MenuItem[]> {
    const prepareItem = item => {
      delete item.expanded;
      delete item.error;
      delete item.warning;

      if (item.children) {
        item.children.map(child => prepareItem(child));
      } else {
        return;
      }
    };

    items.map(item => prepareItem(item));

    return this.menuApi.updateMenus(items);
  }
}

export default new MenuService;
