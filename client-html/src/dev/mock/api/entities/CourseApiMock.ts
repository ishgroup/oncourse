import { promiseResolve } from "../../MockAdapter";

export function CourseApiMock() {
  this.api.onGet(new RegExp(`v1/list/entity/course/\\d+`)).reply(config => {
    const params = config.url.split("/");
    const id = params[params.length - 1];
    return promiseResolve(config, this.db.getCourse(id));
  });

  this.api.onPost("v1/list/entity/course").reply(config => {
    this.db.addCourse(config.data);
    return promiseResolve(config, this.db.getPlainCourses());
  });

  this.api.onDelete(new RegExp(`v1/list/entity/course/\\d+`)).reply(config => {
    const params = config.url.split("/");
    const id = params[params.length - 1];
    this.db.removeCourse(id);
    return promiseResolve(config, this.db.getPlainCourses());
  });
}
