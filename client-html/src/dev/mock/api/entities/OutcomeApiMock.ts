import { promiseResolve } from "../../MockAdapter";
import { getParamsId } from "../../mockUtils";

export function OutcomeApiMock(mock) {
  this.api.onGet(new RegExp(`v1/list/entity/outcome/\\d+`)).reply(config => {
    const id = getParamsId(config);
    return promiseResolve(config, this.db.getOutcome(id));
  });

  this.api.onPut(new RegExp(`v1/list/entity/outcome/\\d+`)).reply(config => promiseResolve(config, JSON.parse(config.data)));

  this.api.onPost("v1/list/entity/outcome").reply(config => promiseResolve(config, JSON.parse(config.data)));
  
  this.api.onDelete(new RegExp(`v1/list/entity/outcome/\\d+`)).reply(config => {
    const id = getParamsId(config);
    this.db.removeOutcome(id);
    return promiseResolve(config, {});
  });
}
