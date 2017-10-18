import faker from "faker";
import {promiseReject, promiseResolve} from "../MockAdapter";
import {API} from "../../../js/constants/Config";

export function blockApiMock(mock) {
  this.api.onGet(API.GET_BLOCKS).reply(config => promiseResolve(
    config,
    this.db.blocks,
  ));

  this.api.onPost(API.SAVE_BLOCK).reply(config => {
    const request = JSON.parse(config.data);

    if (!request.title) {
      return promiseReject(config, {message: 'Title can not be blank'});
    }

    if (!request.id) {

      request.id = faker.random.number();
      this.db.addBlock(request);

    } else {
      this.db.editBlock(request);
    }


    return promiseResolve(
      config,
      request,
    );
  });


  this.api.onPost(API.DELETE_BLOCK).reply(config => {

    this.db.deleteBlockById(JSON.parse(config.data));

    return promiseResolve(
      config,
      null,
    );
  });
}
