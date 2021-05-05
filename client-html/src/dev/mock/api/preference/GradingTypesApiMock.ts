import { promiseResolve } from "../../MockAdapter";
import { getParamsId } from "../../mockUtils";

export function GradingTypesApiMock() {
  this.api.onGet("v1/preference/grading").reply(config => {
    return promiseResolve(config, this.db.getGradingTypes());
  });

  this.api.onPost("v1/preference/grading").reply(config => {
    return promiseResolve(config, this.db.getGradingTypes());
  });

  this.api.onDelete(new RegExp('v1/preference/grading/\\d+')).reply(config => {
    const id = getParamsId(config);
    this.db.removeGradingType(id);
    return promiseResolve(config, this.db.getGradingTypes());
  });
}
