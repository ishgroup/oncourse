import { promiseResolve } from "../../MockAdapter";
import { getParamsId } from "../../mockUtils";

export function AccountApiMock(mock) {
  this.api.onGet(new RegExp(`v1/list/entity/account/\\d+`)).reply(config => {
    const id = getParamsId(config);
    return promiseResolve(config, this.db.getAccount(id));
  });

  this.api.onPut(new RegExp(`v1/list/entity/account/\\d+`)).reply(config => promiseResolve(config, JSON.parse(config.data)));

  this.api.onPost("/v1/list/entity/account").reply(config => promiseResolve(config, JSON.parse(config.data)));

  this.api.onGet(new RegExp(`v1/list/entity/account/depositAccounts`)).reply(config => promiseResolve(config, this.db.getAccounts()));

  this.api.onDelete(new RegExp(`v1/list/entity/account/\\d+`)).reply(config => {
    const id = getParamsId(config);
    this.db.removeAccount(id);
    return promiseResolve(config, {});
  });
}
