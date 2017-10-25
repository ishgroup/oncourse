import {HttpService} from "../common/services/HttpService";
import {CommonError} from "../model/common/CommonError";
import {CourseParams, Course, CourseClassesParams, CourseClass} from "../model";

export class CourseClassesApi {
  constructor(private http: HttpService) {
  }

  getCourseClasses(courseClassesParams: CourseClassesParams): Promise<CourseClass[]> {
    return this.http.POST(`/classes`, courseClassesParams);
  }

  getCourses(courseParams: CourseParams): Promise<Course[]> {
    return this.http.POST('/courses', courseParams);
  }
}
