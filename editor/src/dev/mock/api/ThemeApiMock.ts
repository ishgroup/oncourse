import faker from 'faker';
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

    if (!request.id) {

      request.id = faker.random.number();
      this.db.addTheme(request);

    } else {
      this.db.editTheme(request);
    }

    return promiseResolve(
      config,
      request,
    );
  });

  this.api.onPost(API.ADD_THEME).reply(config => {
    return promiseResolve(
      config,
      this.db.createNewTheme(),
    );
  });

  this.api.onPost(API.DELETE_THEME).reply(config => {
    const id = config.url.split('/')[1];

    this.db.deleteThemeById(id);
    return promiseResolve(
      config,
      null,
    );
  });

  this.api.onGet(API.GET_LAYOUTS).reply(config => promiseResolve(
    config,
    this.db.layouts,
  ));
}
