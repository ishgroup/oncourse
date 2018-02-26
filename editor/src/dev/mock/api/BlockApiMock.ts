import {promiseReject, promiseResolve} from "../MockAdapter";
import {API} from "../../../js/constants/Config";

export function blockApiMock(mock) {
  this.api.onGet(API.BLOCK).reply(config => promiseResolve(
    config,
    this.db.blocks,
  ));

  this.api.onPut(API.BLOCK).reply(config => {
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

  this.api.onPost(API.BLOCK).reply(config => {
    return promiseResolve(
      config,
      this.db.createNewBlock(),
    );
  });


  this.api.onDelete(API.BLOCK_DELETE).reply(config => {
    const id = config.url.split('/')[2];

    this.db.deleteBlockById(id);
    return promiseResolve(
      config,
      null,
    );
  });
}
