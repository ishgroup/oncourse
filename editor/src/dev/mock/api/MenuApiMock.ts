import {promiseResolve} from "../MockAdapter";
import {API} from "../../../js/constants/Config";

export function menuApiMock() {
  this.api.onGet(API.MENU).reply(config => promiseResolve(
    config,
    this.db.menus,
  ));

  this.api.onPut(API.MENU).reply(config => {
    const items = JSON.parse(config.data);
    this.db.saveMenu(items);

    return promiseResolve(
      config,
      items,
    );
  });
}
