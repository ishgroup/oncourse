import { getParamsId } from "../../mockUtils";
import { promiseResolve } from "../../MockAdapter";

export function TutorRoleApiMock(mock) {
  this.api.onGet(new RegExp(`v1/list/entity/definedTutorRole/\\d+`)).reply(config => {
    const id = getParamsId(config);
    return promiseResolve(config, this.db.getTutorRole(id));
  });

  this.api.onPost("v1/list/entity/definedTutorRole").reply(config => {
    this.db.createTutorRole(config.data);
    return promiseResolve(config, {});
  });

  this.api.onPut(new RegExp(`v1/list/entity/definedTutorRole/\\d+`)).reply(config => promiseResolve(config, JSON.parse(config.data)));

  this.api.onDelete(new RegExp(`v1/list/entity/definedTutorRole/\\d+`)).reply(config => {
    const id = getParamsId(config);
    this.db.removeTutorRole(id);
    return promiseResolve(config, {});
  });
}