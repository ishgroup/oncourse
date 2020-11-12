import { promiseResolve } from "../../MockAdapter";

export function AssessmentApiMock(mock) {
  this.api.onGet(new RegExp(`v1/list/entity/assessment/\\d+`)).reply(config => {
    const params = config.url.split("/");
    const id = params[params.length - 1];
    return promiseResolve(config, this.db.getAssessment(id));
  });

  this.api.onPut(new RegExp(`v1/list/entity/assessment/\\d+`)).reply(config => {
    return promiseResolve(config, JSON.parse(config.data));
  });

  this.api.onPost("v1/list/entity/assessment").reply(config => {
    this.db.createAssessment(config.data);
    return promiseResolve(config, this.db.getAssessments());
  });

  this.api.onDelete(new RegExp(`v1/list/entity/assessment/\\d+`)).reply(config => {
    const params = config.url.split("/");
    const id = params[params.length - 1];
    this.db.removeAssessment(id);
    return promiseResolve(config, this.db.getAssessments());
  });
}
