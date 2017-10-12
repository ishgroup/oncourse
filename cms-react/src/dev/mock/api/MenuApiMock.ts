import {promiseResolve} from "../MockAdapter";
import {API} from "../../../js/constants/Config";

export function menuApiMock() {
  this.api.onGet(API.GET_MENU).reply(config => promiseResolve(
    config,
    this.db.menus,
  ));

  this.api.onPost(API.SAVE_MENU).reply(config => {
    const items = JSON.parse(config.data).items;
    this.db.saveMenu(items);

    return promiseResolve(
      config,
      items,
    );
  });
}
