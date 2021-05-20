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

  this.api.onPost("v1/list/option/message/recipients").reply(config => {
    const entity = config.params.entity;
    return promiseResolve(config, this.db.getRecipientsByEntityName(entity));
  });

  this.api.onPost("v1/list/option/message").reply(config => {
    const messageType = config.params.messageType;
    return promiseResolve(config, this.db.getMessageByType(messageType));
  });
}
