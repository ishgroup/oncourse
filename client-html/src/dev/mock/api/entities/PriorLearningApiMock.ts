import { getParamsId } from "../../mockUtils";
import { promiseResolve } from "../../MockAdapter";

export function PriorLearningApiMock() {
  this.api.onGet(new RegExp(`v1/list/entity/priorLearning/\\d+`)).reply(config => {
    const id = getParamsId(config);
    return promiseResolve(config, this.db.getPriorLearning(id));
  });

  this.api.onPost("v1/list/entity/priorLearning").reply(config => {
    this.db.createPriorLearning(config.data);
    return promiseResolve(config, {});
  });
}
