import {HttpService} from "../common/services/HttpService";
import {MenuItem} from "../model/MenuItem";
import {CommonError} from "../model/common/CommonError";

export class MenuApi {
  constructor(private http: HttpService) {
  }

  getMenuItems(): Promise<MenuItem[]> {
    return this.http.GET(`/getMenuItems`);
  }
  saveMenuItems(menu: MenuItem[]): Promise<MenuItem[]> {
    return this.http.POST(`/saveMenuItems`, menu);
  }
}
