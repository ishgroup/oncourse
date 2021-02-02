import { promiseResolve } from "../../MockAdapter";
import { getParamsId } from "../../mockUtils";

export function SurveyApiMock(mock) {
  this.api.onGet(new RegExp(`v1/list/entity/survey/\\d+`)).reply(config => {
    const id = getParamsId(config);
    return promiseResolve(config, this.db.getSurvey(id));
  });

  this.api.onPut(new RegExp(`v1/list/entity/survey/\\d+`)).reply(config => promiseResolve(config, JSON.parse(config.data)));
}
