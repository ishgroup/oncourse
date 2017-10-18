import faker from 'faker';
import {promiseReject, promiseResolve} from "../MockAdapter";
import {API} from "../../../js/constants/Config";

export function pageApiMock() {
  this.api.onGet(API.GET_PAGES).reply(config => promiseResolve(
    config,
    this.db.pages,
  ));

  this.api.onPost(API.SAVE_PAGE).reply(config => {
    const request = JSON.parse(config.data);

    if (!request.title) {
      return promiseReject(config, {message: 'Title can not be blank'});
    }

    if (!request.id) {

      request.id = faker.random.number();
      this.db.addPage(request);

    } else {
      this.db.editPage(request);
    }

    return promiseResolve(
      config,
      request,
    );
  });


  this.api.onPost(API.DELETE_PAGE).reply(config => {

    this.db.deletePageById(JSON.parse(config.data));

    return promiseResolve(
      config,
      null,
    );
  });
}
