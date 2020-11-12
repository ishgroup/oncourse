import { promiseResolve } from "../../MockAdapter";

export function SurveyApiMock(mock) {
  this.api.onGet(new RegExp(`v1/list/entity/survey/\\d+`)).reply(config => {
    const params = config.url.split("/");
    const id = params[params.length - 1];
    return promiseResolve(config, this.db.getSurvey(id));
  });

  this.api.onPut(new RegExp(`v1/list/entity/survey/\\d+`)).reply(config => {
    return promiseResolve(config, JSON.parse(config.data));
  });

  this.api.onDelete(new RegExp(`v1/list/entity/survey/\\d+`)).reply(config => {
    const params = config.url.split("/");
    const id = params[params.length - 1];
    this.db.removeSurvey(id);
    return promiseResolve(config, this.db.getSurveys());
  });
}
