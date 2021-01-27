import { promiseResolve } from "../../MockAdapter";
import { getParamsId } from "../../mockUtils";

export function ApplicationApiMock(mock) {
  this.api.onGet(new RegExp(`v1/list/entity/application/\\d+`)).reply(config => {
    const id = getParamsId(config);
    return promiseResolve(config, this.db.getApplication(id));
  });

  this.api.onPut(new RegExp(`v1/list/entity/application/\\d+`)).reply(config => promiseResolve(config, JSON.parse(config.data)));

  this.api.onPost("v1/list/entity/application").reply(config => {
    this.db.createApplication(config.data);
    return promiseResolve(config, this.db.getApplications());
  });

  this.api.onDelete(new RegExp(`v1/list/entity/application/\\d+`)).reply(config => {
    const id = getParamsId(config);
    this.db.removeApplication(id);
    return promiseResolve(config, {});
  });
}