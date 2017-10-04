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

    return promiseResolve(
      config,
      JSON.parse(config.data),
    );
  });


  this.api.onPost(API.DELETE_BLOCK).reply(config => {
    return promiseResolve(
      config,
      null,
    );
  });
}
