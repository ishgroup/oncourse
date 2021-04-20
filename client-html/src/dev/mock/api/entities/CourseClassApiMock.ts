import { promiseResolve } from "../../MockAdapter";
import { getParamsId } from "../../mockUtils";

export function CourseClassApiMock(mock) {
  this.api.onPost("/v1/list/entity/courseClass/duplicate").reply(config => {
    return promiseResolve(config, JSON.parse(config.data));
  });

  this.api.onGet(new RegExp("/v1/list/entity/courseClass/\\d+")).reply(config => {
    const id = getParamsId(config);
    return promiseResolve(config, this.db.getCourseClass(id));
  });

  this.api.onGet(new RegExp("/v1/list/entity/courseClass/budget/\\d+")).reply(config => {
    return promiseResolve(config, this.db.getCourseClassBudget());
  });
}
