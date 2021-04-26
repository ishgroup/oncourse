import { promiseResolve } from "../../MockAdapter";

export function GradingTypesApiMock() {
  this.api.onGet("v1/preference/grading").reply(config => {
    return promiseResolve(config, this.db.getGradingTypes());
  });
}
