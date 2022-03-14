import { promiseResolve } from "../../MockAdapter";
import { getParamsId } from "../../mockUtils";

export function CourseClassApiMock(mock) {
  this.api.onPost("/v1/list/entity/courseClass/duplicate").reply(config => promiseResolve(config, JSON.parse(config.data)));

  this.api.onGet(new RegExp("/v1/list/entity/courseClass/\\d+")).reply(config => {
    const id = getParamsId(config);
    return promiseResolve(config, this.db.getCourseClass(id));
  });

  this.api.onPost("/v1/list/entity/courseClass").reply(config => promiseResolve(config, this.db.getCourseClassesTotalRows()));

  this.api.onPost("/v1/list/entity/courseClass/cancel").reply(config => promiseResolve(config, {}));

  this.api.onGet(new RegExp("/v1/list/entity/courseClass/budget/\\d+")).reply(config => promiseResolve(config, this.db.getCourseClassBudget()));

  this.api.onGet(new RegExp("/v1/list/entity/courseClass/timetable/\\d+")).reply(config => promiseResolve(config, this.db.getCourseClassTimetable()));

  this.api.onPost(new RegExp("/v1/list/entity/courseClass/timetable/\\d+")).reply(config => promiseResolve(config, this.db.getCourseClassTimetableSessions()));

  this.api.onGet(new RegExp("/v1/list/entity/courseClass/tutor/\\d+")).reply(config => promiseResolve(config, this.db.getCourseClassTutors()));

  this.api.onGet(new RegExp("/v1/list/entity/courseClass/assessment/\\d+")).reply(config => promiseResolve(config, this.db.getCourseClassAssessment()));

  this.api.onGet(new RegExp("/v1/list/entity/courseClass/attendance/student/\\d+")).reply(config => promiseResolve(config, this.db.getCourseClassAttendanceStudents()));

  this.api.onGet(new RegExp("/v1/list/entity/courseClass/trainingPlan/\\d+")).reply(config => promiseResolve(config, this.db.getCourseClassTrainingPlan()));
}
