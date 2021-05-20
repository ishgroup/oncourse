import { promiseResolve } from "../../MockAdapter";
import { getParamsId } from "../../mockUtils";

export function CourseApiMock() {
  this.api.onGet(new RegExp(`v1/list/entity/course/\\d+`)).reply(config => {
    const id = getParamsId(config);
    return promiseResolve(config, this.db.getCourse(id));
  });

  this.api.onPost("v1/list/entity/course").reply(config => {
    this.db.addCourse(config.data);
    return promiseResolve(config, this.db.getCourses());
  });

  this.api.onPut(new RegExp(`v1/list/entity/course/\\d+`)).reply(config => promiseResolve(config, {}));

  this.api.onPost("v1/list/entity/course/duplicate").reply(config => {
    this.db.duplicateCourse(JSON.parse(config.data));
    return promiseResolve(config, this.db.getCourses());
  });

  this.api.onDelete(new RegExp(`v1/list/entity/course/\\d+`)).reply(config => {
    const id = getParamsId(config);
    this.db.removeCourse(id);
    return promiseResolve(config, {});
  });
}
