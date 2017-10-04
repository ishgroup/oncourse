import {promiseReject, promiseResolve} from "../MockAdapter";
import {API} from "../../../js/constants/Config";

export function themeApiMock() {
  this.api.onGet(API.GET_THEMES).reply(config => promiseResolve(
    config,
    this.db.themes,
  ));

  this.api.onPost(API.SAVE_THEME).reply(config => {
    const request = JSON.parse(config.data);

    if (!request.title) {
      return promiseReject(config, {message: 'Title can not be blank'});
    }

    return promiseResolve(
      config,
      JSON.parse(config.data),
    );
  });

  this.api.onPost(API.DELETE_THEME).reply(config => {

    this.db.deleteThemeById(JSON.parse(config.data));

    return promiseResolve(
      config,
      null,
    );
  });
}
