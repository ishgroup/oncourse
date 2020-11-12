import { promiseResolve } from "../../MockAdapter";

export function CourseClassApiMock(mock) {
  this.api.onPost("/v1/list/entity/courseClass/duplicate").reply(config => {
    return promiseResolve(config, JSON.parse(config.data));
  });

  this.api.onGet(new RegExp("/v1/list/entity/courseClass/\\d+")).reply(config => {
    const params = config.url.split("/");
    const id = params[params.length - 1];
    return promiseResolve(config, this.db.getCourseClass(id));
  });
}
