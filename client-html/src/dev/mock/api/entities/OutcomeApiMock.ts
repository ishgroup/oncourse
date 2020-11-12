import { promiseResolve } from "../../MockAdapter";

export function OutcomeApiMock(mock) {
  this.api.onGet(new RegExp(`v1/list/entity/outcome/\\d+`)).reply(config => {
    const params = config.url.split("/");
    const id = params[params.length - 1];
    return promiseResolve(config, this.db.getOutcome(id));
  });

  this.api.onPut(new RegExp(`v1/list/entity/outcome/\\d+`)).reply(config => {
    return promiseResolve(config, JSON.parse(config.data));
  });
}
