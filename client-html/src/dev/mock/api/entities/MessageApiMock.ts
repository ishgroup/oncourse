import { promiseResolve } from "../../MockAdapter";
import { getParamsId } from "../../mockUtils";

export function MessageApiMock(mock) {
  this.api.onGet(new RegExp(`v1/list/entity/message/\\d+`)).reply(config => {
    const id = getParamsId(config);
    return promiseResolve(config, this.db.getMessage(id));
  });

  this.api.onPut(new RegExp(`v1/list/entity/message/\\d+`)).reply(config => promiseResolve(config, JSON.parse(config.data)));

  this.api.onDelete(new RegExp(`v1/list/entity/message/\\d+`)).reply(config => {
    const id = getParamsId(config);
    this.db.removeMessage(id);
    return promiseResolve(config, {});
  });
}
