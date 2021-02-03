import { promiseResolve } from "../../MockAdapter";
import { getParamsId } from "../../mockUtils";

export function WaitingListApiMock(mock) {
  /**
   * List items
   * */
  this.api.onGet(new RegExp(`v1/list/entity/waitingList/\\d+`)).reply(config => {
    const id = getParamsId(config);
    return promiseResolve(config, this.db.getWaitingList(id));
  });

  this.api.onPut(new RegExp(`v1/list/entity/waitingList/\\d+`)).reply(config => promiseResolve(config, JSON.parse(config.data)));

  this.api.onPost("v1/list/entity/waitingList").reply(config => {
    this.db.createWaitingList(config.data);
    return promiseResolve(config, {});
  });
}
