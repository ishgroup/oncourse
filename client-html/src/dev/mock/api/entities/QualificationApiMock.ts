import { promiseResolve } from "../../MockAdapter";
import { getParamsId } from "../../mockUtils";

export function QualificationApiMock() {
  this.api.onGet(new RegExp(`v1/list/entity/qualification/\\d+`)).reply(config => {
    const id = getParamsId(config);
    return promiseResolve(config, this.db.getQualification(id));
  });

  this.api.onPut(new RegExp(`v1/list/entity/qualification/\\d+`)).reply(config => promiseResolve(config, JSON.parse(config.data)));

  this.api.onPost("v1/list/entity/qualification").reply(config => {
    this.db.createQualification(config.data);
    return promiseResolve(config, {});
  });

  this.api.onDelete(new RegExp(`v1/list/entity/qualification/\\d+`)).reply(config => {
    const id = getParamsId(config);
    this.db.removeQualification(id);
    return promiseResolve(config, {});
  });
}
