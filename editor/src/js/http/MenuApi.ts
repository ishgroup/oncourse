import {HttpService} from "../common/services/HttpService";
import {MenuItem} from "../model/MenuItem";
import {CommonError} from "../model/common/CommonError";

export class MenuApi {
  constructor(private http: HttpService) {
  }

  getMenuItems(): Promise<MenuItem[]> {
    return this.http.GET(`/menu.list`);
  }
  saveMenuItems(menu: MenuItem[]): Promise<MenuItem[]> {
    return this.http.POST(`/menu.update`, menu);
  }
}
