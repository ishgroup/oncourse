import faker from 'faker';
import {promiseReject, promiseResolve} from "../MockAdapter";
import {API} from "../../../js/constants/Config";

export function themeApiMock() {
  this.api.onGet(API.THEME).reply(config => promiseResolve(
    config,
    this.db.themes,
  ));

  this.api.onPut(API.THEME).reply(config => {
    const request = JSON.parse(config.data);

    if (!request.title) {
      return promiseReject(config, {message: 'Title can not be blank'});
    }

    if (!request.id) {

      request.id = faker.datatype.number();
      this.db.addTheme(request);

    } else {
      this.db.editTheme(request);
    }

    return promiseResolve(
      config,
      request,
    );
  });

  this.api.onPost(API.THEME).reply(config => {
    return promiseResolve(
      config,
      this.db.createNewTheme(),
    );
  });

  this.api.onDelete(API.THEME_DELETE).reply(config => {
    const id = config.url.split('/')[2];

    this.db.deleteThemeById(id);
    return promiseResolve(
      config,
      null,
    );
  });

  this.api.onGet(API.LAYOUT).reply(config => promiseResolve(
    config,
    this.db.layouts,
  ));
}
