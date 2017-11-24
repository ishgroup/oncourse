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

    if (this.db.blocks.filter(block => block.id !== request.id).find(block => block.title === request.title)) {
      return promiseReject(config, {message: 'Block title already used'});
    }

    this.db.editBlock(request);

    return promiseResolve(
      config,
      request,
    );
  });

  this.api.onPost(API.ADD_BLOCK).reply(config => {
    return promiseResolve(
      config,
      this.db.createNewBlock(),
    );
  });


  this.api.onPost(API.DELETE_BLOCK).reply(config => {
    this.db.deleteBlockByTitle(config.data);

    return promiseResolve(
      config,
      null,
    );
  });
}
