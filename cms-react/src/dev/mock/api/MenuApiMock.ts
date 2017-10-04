import {promiseResolve} from "../MockAdapter";
import {API} from "../../../js/constants/Config";

export function menuApiMock() {
  this.api.onGet(API.GET_MENU).reply(config => promiseResolve(
    config,
    this.db.menus,
  ));
}
